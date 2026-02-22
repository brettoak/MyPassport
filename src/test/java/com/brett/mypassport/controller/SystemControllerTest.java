package com.brett.mypassport.controller;

import com.brett.mypassport.common.JwtUtil;
import com.brett.mypassport.common.PermissionConstants;
import com.brett.mypassport.config.DatabaseSeeder;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.flywaydb.core.api.output.CleanResult;
import org.flywaydb.core.api.output.MigrateResult;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.brett.mypassport.config.RsaKeyProperties; // Depending on context if you need mock
import com.brett.mypassport.config.SecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(SystemController.class)
@Import(SecurityConfig.class)
public class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Flyway flyway;

    @MockBean
    private DatabaseSeeder databaseSeeder;

    // We mock JwtUtil and RsaKeyProperties to bypass the security configuration that looks for keys.
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RsaKeyProperties rsaKeyProperties;

    @BeforeEach
    public void setUp() {
        // Clear any startup invocations (e.g. ApplicationRunner interface)
        Mockito.clearInvocations(flyway, databaseSeeder);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.SYS_CONFIG_EDIT})
    public void testResetDatabaseSuccess() throws Exception {
        // Arrange
        when(flyway.clean()).thenReturn(new CleanResult("", ""));
        when(flyway.migrate()).thenReturn(new MigrateResult("", "", "", ""));
        doNothing().when(databaseSeeder).run(any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/system/reset-database").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("Database reset and seeded successfully."));
        
        verify(flyway, times(1)).clean();
        verify(flyway, times(1)).migrate();
        verify(databaseSeeder, times(1)).run(null);
    }

    @Test
    @WithMockUser(username = "user", authorities = {PermissionConstants.USER_VIEW})
    public void testResetDatabaseForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/system/reset-database").with(csrf()))
                .andExpect(status().isForbidden());
        
        verify(flyway, never()).clean();
        verify(flyway, never()).migrate();
        verify(databaseSeeder, never()).run(any());
    }
}
