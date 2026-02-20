package com.brett.mypassport.repository;

import com.brett.mypassport.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find a role by its unique name.
     * @param name The name of the role (e.g., ADMIN)
     * @return An Optional containing the Role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Check if a role exists by its name.
     * @param name The name of the role
     * @return true if the role exists, false otherwise
     */
    boolean existsByName(String name);
}
