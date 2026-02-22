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
import com.brett.mypassport.common.PermissionConstants;

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
        System.out.println("Seeding/Updating permissions...");
        // User Management Permissions
        Permission userView = createOrUpdatePermission(PermissionConstants.USER_VIEW, "View user details and lists", "USER_MANAGEMENT");
        Permission userCreate = createOrUpdatePermission(PermissionConstants.USER_CREATE, "Create new users", "USER_MANAGEMENT");
        Permission userUpdate = createOrUpdatePermission(PermissionConstants.USER_UPDATE, "Edit user details", "USER_MANAGEMENT");
        Permission userDelete = createOrUpdatePermission(PermissionConstants.USER_DELETE, "Delete users", "USER_MANAGEMENT");

        // Role Management Permissions
        Permission roleView = createOrUpdatePermission(PermissionConstants.ROLE_VIEW, "View roles and permissions", "ROLE_MANAGEMENT");
        Permission roleCreate = createOrUpdatePermission(PermissionConstants.ROLE_CREATE, "Create new roles", "ROLE_MANAGEMENT");
        Permission roleUpdate = createOrUpdatePermission(PermissionConstants.ROLE_UPDATE, "Edit roles", "ROLE_MANAGEMENT");
        Permission roleDelete = createOrUpdatePermission(PermissionConstants.ROLE_DELETE, "Delete roles", "ROLE_MANAGEMENT");
        Permission roleAssign = createOrUpdatePermission(PermissionConstants.ROLE_ASSIGN, "Assign roles to users", "ROLE_MANAGEMENT");

        // Device/Session Management Permissions
        Permission deviceView = createOrUpdatePermission(PermissionConstants.DEVICE_VIEW, "View active sessions and devices", "DEVICE_MANAGEMENT");
        Permission deviceKick = createOrUpdatePermission(PermissionConstants.DEVICE_KICK, "Terminate active sessions", "DEVICE_MANAGEMENT");

        // System Configuration Permissions
        Permission sysConfigView = createOrUpdatePermission(PermissionConstants.SYS_CONFIG_VIEW, "View system configurations", "SYSTEM_CONFIG");
        Permission sysConfigEdit = createOrUpdatePermission(PermissionConstants.SYS_CONFIG_EDIT, "Edit system configurations", "SYSTEM_CONFIG");
        
        List<Permission> allPermissions = Arrays.asList(
                userView, userCreate, userUpdate, userDelete,
                roleView, roleCreate, roleUpdate, roleDelete, roleAssign,
                deviceView, deviceKick,
                sysConfigView, sysConfigEdit
        );
        
        permissionRepository.saveAll(allPermissions);

        System.out.println("Seeding/Updating roles...");
        Role adminRole = createOrUpdateRole("ADMIN", "System Administrator");
        // Admin gets all permissions
        adminRole.setPermissions(new HashSet<>(allPermissions));
        roleRepository.save(adminRole);

        Role userRole = createOrUpdateRole("USER", "Standard User");
        // Standard User gets basic view permissions
        userRole.setPermissions(new HashSet<>(Arrays.asList(deviceView)));
        roleRepository.save(userRole);
    }

    private Permission createOrUpdatePermission(String name, String description, String module) {
        Permission p = permissionRepository.findByName(name).orElseGet(Permission::new);
        p.setName(name);
        p.setDescription(description);
        p.setModule(module);
        return p;
    }

    private Role createOrUpdateRole(String name, String description) {
        Role r = roleRepository.findByName(name).orElseGet(Role::new);
        r.setName(name);
        r.setDescription(description);
        return r;
    }

    private void seedUsers() {
        System.out.println("Checking database users...");
        
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role userRole = roleRepository.findByName("USER").orElse(null);

        // Seed Admin User
        User admin = userRepository.findByUsername("admin@example.com").orElseGet(User::new);
        admin.setUsername("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        if (adminRole != null) {
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
        }
        userRepository.save(admin);
        System.out.println("Seeded/Updated admin@example.com");

        // Seed Standard User
        boolean isFirstTimeUserSeeding = !userRepository.existsByUsername("user@example.com");
        
        User user = userRepository.findByUsername("user@example.com").orElseGet(User::new);
        user.setUsername("user@example.com");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEmail("user@example.com");
        if (userRole != null) {
            user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        }
        userRepository.save(user);
        System.out.println("Seeded/Updated user@example.com");
        
        // Only seed random users if we are seeding the main test user for the first time
        if (isFirstTimeUserSeeding) {
            for (int i = 0; i < 2; i++) {
                User randomUser = new User();
                randomUser.setUsername(faker.name().username());
                randomUser.setPassword(passwordEncoder.encode("123"));
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
