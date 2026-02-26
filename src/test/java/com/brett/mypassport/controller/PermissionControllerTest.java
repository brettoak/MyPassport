package com.brett.mypassport.controller;

import com.brett.mypassport.dto.PermissionResponse;
import com.brett.mypassport.service.PermissionService;
import com.brett.mypassport.common.JwtUtil;
import com.brett.mypassport.common.PermissionConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.brett.mypassport.config.SecurityConfig;
import com.brett.mypassport.config.RsaKeyProperties;

import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.mockito.ArgumentMatchers.any;
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

    @MockBean
    private RsaKeyProperties rsaKeyProperties;

    @Test
    @WithMockUser(username = "admin", authorities = {PermissionConstants.PERMISSION_VIEW})
    public void testGetAllPermissions() throws Exception {
        PermissionResponse perm1 = new PermissionResponse();
        perm1.setId(1L);
        perm1.setName("USER_CREATE");
        perm1.setModule("USER_MANAGEMENT");

        PermissionResponse perm2 = new PermissionResponse();
        perm2.setId(2L);
        perm2.setName("PERMISSION_VIEW");
        perm2.setModule("ROLE_MANAGEMENT");

        List<PermissionResponse> permissions = Arrays.asList(perm1, perm2);
        Page<PermissionResponse> permissionPage = new PageImpl<>(permissions);

        when(permissionService.getAllPermissions(any(), any(Pageable.class))).thenReturn(permissionPage);

        mockMvc.perform(get("/api/v1/permissions")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("USER_CREATE"))
                .andExpect(jsonPath("$.data.content[1].name").value("PERMISSION_VIEW"));
    }
}
