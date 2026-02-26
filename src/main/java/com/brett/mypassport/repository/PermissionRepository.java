package com.brett.mypassport.repository;

import com.brett.mypassport.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permissions by module name.
     * @param module The module name (e.g., USER_MANAGEMENT)
     * @return List of permissions in the module
     */
    List<Permission> findByModule(String module);

    /**
     * Find a permission by its unique name.
     * @param name The name of the permission
     * @return An Optional containing the Permission if found
     */
    Optional<Permission> findByName(String name);

    /**
     * Find permissions by system code with pagination.
     */
    org.springframework.data.domain.Page<Permission> findBySysCode(String sysCode, org.springframework.data.domain.Pageable pageable);
}
