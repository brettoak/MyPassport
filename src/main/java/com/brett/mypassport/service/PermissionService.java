package com.brett.mypassport.service;

import com.brett.mypassport.dto.PermissionResponse;
import com.brett.mypassport.entity.Permission;
import com.brett.mypassport.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Retrieve all permissions in the system.
     * @return Page of all mapping permissions
     */
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getAllPermissions(String sysCode, Pageable pageable) {
        if (sysCode != null && !sysCode.isEmpty()) {
            return permissionRepository.findBySysCode(sysCode, pageable)
                    .map(this::mapToResponse);
        }
        return permissionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Helper method to map Permission entity to PermissionResponse DTO.
     */
    private PermissionResponse mapToResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        response.setModule(permission.getModule());
        response.setSysCode(permission.getSysCode());
        return response;
    }
}
