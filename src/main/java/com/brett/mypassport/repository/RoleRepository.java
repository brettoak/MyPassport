package com.brett.mypassport.repository;

import com.brett.mypassport.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find a role by its unique name within a specific system.
     * @param name The name of the role (e.g., ADMIN)
     * @param sysCode The system code
     * @return An Optional containing the Role if found
     */
    Optional<Role> findByNameAndSysCode(String name, String sysCode);

    Optional<Role> findByName(String name);

    /**
     * Check if a role exists by its name within a specific system.
     * @param name The name of the role
     * @param sysCode The system code
     * @return true if the role exists, false otherwise
     */
    boolean existsByNameAndSysCode(String name, String sysCode);

    boolean existsByName(String name);

    /**
     * Find roles by system code with pagination.
     */
    org.springframework.data.domain.Page<Role> findBySysCode(String sysCode, org.springframework.data.domain.Pageable pageable);
}
