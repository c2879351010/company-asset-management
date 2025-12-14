package com.example.backend.auth;

import com.example.backend.dto.users.AdminUserUpdateDTO;
import com.example.backend.dto.users.UserUpdateDTOBase;
import com.example.backend.entity.Users;
import com.example.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@MapperScan("com.example.backend.dao")
public class UsersAdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersService usersService;
    private Users testUser;
    private Users adminUser;
    private String testUserId;
    private String adminUserId;
    private String rawPassword = "Test@1234";

    @BeforeEach
    void setUp() {

        // 创建测试用户
        testUser = new Users();
        testUser.setUserId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("太郎");
        testUser.setLastName("山田");
        testUser.setFirstKana("タロウ");
        testUser.setLastKana("ヤマダ");
        testUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        testUser.setRole("USER");
        testUser.setCreatedAt(new Date());
        testUser.setUpdatedAt(new Date());
        testUser.setStatus("Active");

        usersService.registerUser(testUser, rawPassword);
        usersService.getUserByEmail("test@example.com");
        testUserId = testUser.getUserId();
        // 创建管理员用户
        adminUser = testUser;
        adminUser.setRole("ADMIN");
        adminUser.setEmail("admin@example.com");
        usersService.registerUser(adminUser, rawPassword);
        usersService.getUserByEmail("admin@example.com");
        adminUserId = adminUser.getUserId();
    }

    public static AdminUserUpdateDTO createAdminUserUpdateDTO(String userId) {
        AdminUserUpdateDTO dto = new AdminUserUpdateDTO();
        setBaseUserUpdateFields(dto, userId);
        dto.setRole("ADMIN");
        dto.setStatus("Active");
        return dto;
    }
    private static void setBaseUserUpdateFields(UserUpdateDTOBase dto, String userId) {
        dto.setUserId(userId);
        dto.setEmail("updated@example.com");
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setFirstKana("アップデート");
        dto.setLastKana("ネーム");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAllUsers_WithAdminRole_ShouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAllUsers_WithFilters_ShouldReturnFilteredUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "test")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateUserByAdmin_WithValidData_ShouldUpdateUser() throws Exception {
        AdminUserUpdateDTO updateDTO = createAdminUserUpdateDTO(testUser.getUserId());

        mockMvc.perform(put("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateUserByAdmin_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        AdminUserUpdateDTO updateDTO = createAdminUserUpdateDTO(testUser.getUserId());
        updateDTO.setEmail("invalid-email"); // 无效邮箱

        mockMvc.perform(put("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getStatus_WithAdminRole_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/admin/users/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void getAllUsers_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}
