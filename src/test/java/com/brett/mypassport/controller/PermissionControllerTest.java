package com.brett.mypassport.controller;

import com.brett.mypassport.dto.PermissionResponse;
import com.brett.mypassport.service.PermissionService;
import com.brett.mypassport.common.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.brett.mypassport.config.SecurityConfig;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PermissionController.class)
@Import(SecurityConfig.class)
public class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "admin")
    public void testGetAllPermissions() throws Exception {
        PermissionResponse perm1 = new PermissionResponse();
        perm1.setId(1L);
        perm1.setName("USER_CREATE");
        perm1.setModule("USER_MANAGEMENT");

        PermissionResponse perm2 = new PermissionResponse();
        perm2.setId(2L);
        perm2.setName("ROLE_VIEW");
        perm2.setModule("ROLE_MANAGEMENT");

        List<PermissionResponse> permissions = Arrays.asList(perm1, perm2);

        when(permissionService.getAllPermissions()).thenReturn(permissions);

        mockMvc.perform(get("/api/v1/permissions")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("USER_CREATE"))
                .andExpect(jsonPath("$.data[1].name").value("ROLE_VIEW"));
    }
}
