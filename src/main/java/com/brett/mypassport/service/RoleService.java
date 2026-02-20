package com.brett.mypassport.service;

import com.brett.mypassport.dto.RoleRequest;
import com.brett.mypassport.dto.RoleResponse;
import com.brett.mypassport.entity.Role;
import com.brett.mypassport.repository.RoleRepository;
import com.brett.mypassport.entity.Permission;
import com.brett.mypassport.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Create a new role.
     * @param request The role details
     * @return The created role response
     */
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Role savedRole = roleRepository.save(role);
        return mapToResponse(savedRole);
    }

    /**
     * Update an existing role.
     * @param id The role ID
     * @param request The new role details
     * @return The updated role response
     */
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " not found"));

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Role updatedRole = roleRepository.save(role);
        return mapToResponse(updatedRole);
    }

    /**
     * Delete a role by its ID.
     * @param id The role ID
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " not found"));
        
        // TODO: In a more complex system, we should verify that this role is not currently 
        // bound to any active users before deleting, or decide on a cascading strategy.
        roleRepository.delete(role);
    }

    /**
     * Retrieve all roles.
     * @return List of role responses
     */
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific role by ID.
     * @param id The role ID
     * @return The role response
     */
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " not found"));
        return mapToResponse(role);
    }

    /**
     * Assign permissions to a role.
     * @param roleId The role ID
     * @param permissionIds List of permission IDs to assign
     */
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + roleId + " not found"));

        Set<Permission> permissions = new HashSet<>();
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissions.addAll(permissionRepository.findAllById(permissionIds));
        }
        
        // This completely replaces any existing permissions with the new set
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    /**
     * Helper method to map Role entity to RoleResponse DTO.
     */
    private RoleResponse mapToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setCreatedAt(role.getCreatedAt());
        return response;
    }
}
