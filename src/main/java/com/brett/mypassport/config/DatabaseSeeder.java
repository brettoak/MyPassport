package com.brett.mypassport.config;

import com.brett.mypassport.entity.User;
import com.brett.mypassport.entity.Role;
import com.brett.mypassport.entity.Permission;
import com.brett.mypassport.repository.UserRepository;
import com.brett.mypassport.repository.RoleRepository;
import com.brett.mypassport.repository.PermissionRepository;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final Faker faker;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.faker = new Faker();
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        seedPermissionsAndRoles();
        seedUsers();
    }

    private void seedPermissionsAndRoles() {
        if (permissionRepository.count() == 0) {
            System.out.println("Seeding permissions...");
            // User Management Permissions
            Permission userView = createPermission("USER_VIEW", "View user details and lists", "USER_MANAGEMENT");
            Permission userCreate = createPermission("USER_CREATE", "Create new users", "USER_MANAGEMENT");
            Permission userUpdate = createPermission("USER_UPDATE", "Edit user details", "USER_MANAGEMENT");
            Permission userDelete = createPermission("USER_DELETE", "Delete users", "USER_MANAGEMENT");

            // Role Management Permissions
            Permission roleView = createPermission("ROLE_VIEW", "View roles and permissions", "ROLE_MANAGEMENT");
            Permission roleCreate = createPermission("ROLE_CREATE", "Create new roles", "ROLE_MANAGEMENT");
            Permission roleUpdate = createPermission("ROLE_UPDATE", "Edit roles", "ROLE_MANAGEMENT");
            Permission roleDelete = createPermission("ROLE_DELETE", "Delete roles", "ROLE_MANAGEMENT");
            Permission roleAssign = createPermission("ROLE_ASSIGN", "Assign roles to users", "ROLE_MANAGEMENT");

            // Device/Session Management Permissions
            Permission deviceView = createPermission("DEVICE_VIEW", "View active sessions and devices", "DEVICE_MANAGEMENT");
            Permission deviceKick = createPermission("DEVICE_KICK", "Terminate active sessions", "DEVICE_MANAGEMENT");

            // System Configuration Permissions
            Permission sysConfigView = createPermission("SYS_CONFIG_VIEW", "View system configurations", "SYSTEM_CONFIG");
            Permission sysConfigEdit = createPermission("SYS_CONFIG_EDIT", "Edit system configurations", "SYSTEM_CONFIG");
            
            List<Permission> allPermissions = Arrays.asList(
                    userView, userCreate, userUpdate, userDelete,
                    roleView, roleCreate, roleUpdate, roleDelete, roleAssign,
                    deviceView, deviceKick,
                    sysConfigView, sysConfigEdit
            );
            
            permissionRepository.saveAll(allPermissions);

            System.out.println("Seeding roles...");
            Role adminRole = createRole("ADMIN", "System Administrator");
            // Admin gets all permissions
            adminRole.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(adminRole);

            Role userRole = createRole("USER", "Standard User");
            // Standard User gets basic view permissions
            userRole.setPermissions(new HashSet<>(Arrays.asList(userView, deviceView)));
            roleRepository.save(userRole);
        } else {
            System.out.println("Permissions and roles already seeded. Skipping...");
        }
    }

    private Permission createPermission(String name, String description, String module) {
        Permission p = new Permission();
        p.setName(name);
        p.setDescription(description);
        p.setModule(module);
        return p;
    }

    private Role createRole(String name, String description) {
        Role r = new Role();
        r.setName(name);
        r.setDescription(description);
        return r;
    }

    private void seedUsers() {
        System.out.println("Checking database users...");
        
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role userRole = roleRepository.findByName("USER").orElse(null);

        // Seed Admin User
        userRepository.findByUsername("admin@example.com").ifPresent(user -> {
            System.out.println("Deleting existing admin@example.com to re-seed...");
            userRepository.delete(user);
        });

        User admin = new User();
        admin.setUsername("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        if (adminRole != null) {
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
        }
        userRepository.save(admin);
        System.out.println("Seeded admin@example.com");

        // Seed Standard User
        boolean isFirstTimeUserSeeding = false;
        if (userRepository.existsByUsername("user@example.com")) {
            System.out.println("Deleting existing user@example.com to re-seed...");
            userRepository.findByUsername("user@example.com").ifPresent(userRepository::delete);
        } else {
            isFirstTimeUserSeeding = true; // only seed random users if the main user didn't exist at all
        }

        User user = new User();
        user.setUsername("user@example.com");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEmail("user@example.com");
        if (userRole != null) {
            user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        }
        userRepository.save(user);
        System.out.println("Seeded user@example.com");
        
        // Only seed random users if we are seeding the main test user for the first time
        if (isFirstTimeUserSeeding) {
            for (int i = 0; i < 2; i++) {
                User randomUser = new User();
                randomUser.setUsername(faker.name().username());
                randomUser.setPassword(passwordEncoder.encode("password"));
                randomUser.setEmail(faker.internet().emailAddress());
                if (userRole != null) {
                    randomUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
                }
                userRepository.save(randomUser);
            }
            System.out.println("Seeded random users.");
        }
    }
}
