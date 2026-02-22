package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.config.DatabaseSeeder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/system")
@Tag(name = "System", description = "APIs for System Administration")
@SecurityRequirement(name = "bearerAuth")
public class SystemController {

    private final Flyway flyway;
    private final DatabaseSeeder databaseSeeder;

    @Autowired
    public SystemController(Flyway flyway, DatabaseSeeder databaseSeeder) {
        this.flyway = flyway;
        this.databaseSeeder = databaseSeeder;
    }

    @Operation(summary = "Reset Database", description = "DANGEROUS: Drops all tables, re-runs migrations, and seeds the initial data.")
    @ApiResponse(responseCode = "200", description = "Database reset successfully")
    @ApiResponse(responseCode = "403", description = "Unauthorized access")
    @Order(100)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).SYS_CONFIG_EDIT)")
    @PostMapping("/reset-database")
    public ResponseEntity<String> resetDatabase() {
        try {
            // 1. Drop all tables
            flyway.clean();

            // 2. Re-run migrations
            flyway.migrate();

            // 3. Re-seed data
            databaseSeeder.run(null);

            return ResponseEntity.ok("Database reset and seeded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to reset database: " + e.getMessage());
        }
    }
}
