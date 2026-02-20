package com.brett.mypassport.repository;

import com.brett.mypassport.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permissions by module name.
     * @param module The module name (e.g., USER_MANAGEMENT)
     * @return List of permissions in the module
     */
    List<Permission> findByModule(String module);
}
