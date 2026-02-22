package com.brett.mypassport.controller;

import com.brett.mypassport.dto.RoleRequest;
import com.brett.mypassport.dto.RoleResponse;
import com.brett.mypassport.service.RoleService;
import com.brett.mypassport.common.JwtUtil;
import com.brett.mypassport.common.PermissionConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.brett.mypassport.config.SecurityConfig;
import com.brett.mypassport.config.RsaKeyProperties;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RsaKeyProperties rsaKeyProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.ROLE_CREATE})
    public void testCreateRole() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");
        request.setDescription("Administrator role");

        RoleResponse response = new RoleResponse();
        response.setId(1L);
        response.setName("ADMIN");
        response.setDescription("Administrator role");
        response.setCreatedAt(LocalDateTime.now());

        when(roleService.createRole(any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("ADMIN"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.ROLE_VIEW})
    public void testGetAllRoles() throws Exception {
        RoleResponse role1 = new RoleResponse();
        role1.setId(1L);
        role1.setName("ADMIN");

        RoleResponse role2 = new RoleResponse();
        role2.setId(2L);
        role2.setName("USER");

        List<RoleResponse> roles = Arrays.asList(role1, role2);
        Page<RoleResponse> rolePage = new PageImpl<>(roles);

        when(roleService.getAllRoles(any(Pageable.class))).thenReturn(rolePage);

        mockMvc.perform(get("/api/v1/roles")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.data.content[1].name").value("USER"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.ROLE_VIEW})
    public void testGetRoleById() throws Exception {
        RoleResponse role = new RoleResponse();
        role.setId(1L);
        role.setName("ADMIN");

        when(roleService.getRoleById(1L)).thenReturn(role);

        mockMvc.perform(get("/api/v1/roles/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.ROLE_UPDATE})
    public void testUpdateRole() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("SUPER_ADMIN");
        request.setDescription("Updated description");

        RoleResponse response = new RoleResponse();
        response.setId(1L);
        response.setName("SUPER_ADMIN");
        response.setDescription("Updated description");

        when(roleService.updateRole(eq(1L), any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("SUPER_ADMIN"))
                .andExpect(jsonPath("$.data.description").value("Updated description"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.ROLE_DELETE})
    public void testDeleteRole() throws Exception {
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/api/v1/roles/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("Role deleted successfully"));
    }
}
