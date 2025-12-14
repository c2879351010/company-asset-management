package com.example.backend.auth;

import com.example.backend.dto.auth.ChangePasswordDTO;
import com.example.backend.dto.users.AdminUserUpdateDTO;
import com.example.backend.dto.users.UserProfileUpdateDTO;
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
public class UserProfileControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersService usersService;
    private Users testUser;

    private String testUserId;
    private final String rawPassword = "oldPassword123";
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
        testUser.setRole("ADMIN");
        testUser.setCreatedAt(new Date());
        testUser.setUpdatedAt(new Date());
        testUser.setStatus("Active");
        usersService.registerUser(testUser, rawPassword);
        usersService.getUserByEmail("test@example.com");
        testUserId = testUser.getUserId();
    }


    public static UserProfileUpdateDTO createUserProfileUpdateDTO(String userId) {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
        setBaseUserUpdateFields(dto, userId);
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

    public static ChangePasswordDTO createChangePasswordDTO(String userId) {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(userId);
        dto.setOldPassword("oldPassword123");
        dto.setNewPassword("newPassword123");
        return dto;
    }
    @Test
    @WithMockUser(username = "test@example.com", roles = {"User"})
    void updateUserByUser_WithValidData_ShouldUpdateProfile() throws Exception {
        UserProfileUpdateDTO updateDTO = createUserProfileUpdateDTO(testUser.getUserId());

        mockMvc.perform(put("/api/users/profile/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"User"})
    void updateUserByUser_WithUserIdMismatch_ShouldReturnBadRequest() throws Exception {
        UserProfileUpdateDTO updateDTO = createUserProfileUpdateDTO("different-user-id");

        mockMvc.perform(put("/api/users/profile/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"User"})
    void updateUserByUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserProfileUpdateDTO updateDTO = createUserProfileUpdateDTO(testUser.getUserId());
        updateDTO.setEmail("invalid-email"); // 无效邮箱

        mockMvc.perform(put("/api/users/profile/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"User"})
    void changePasswordByUser_WithValidData_ShouldChangePassword() throws Exception {
        ChangePasswordDTO changePasswordDTO = createChangePasswordDTO(testUser.getUserId());

        mockMvc.perform(put("/api/users/profile/change-password/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully."));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"User"})
    void changePasswordByUser_WithUserIdMismatch_ShouldReturnBadRequest() throws Exception {
        ChangePasswordDTO changePasswordDTO = createChangePasswordDTO("different-user-id");

        mockMvc.perform(put("/api/users/profile/change-password/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"User"})
    void changePasswordByUser_WithInvalidCurrentPassword_ShouldReturnBadRequest() throws Exception {
        ChangePasswordDTO changePasswordDTO = createChangePasswordDTO(testUser.getUserId());
        changePasswordDTO.setOldPassword("wrong-password");

        mockMvc.perform(put("/api/users/profile/change-password/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserByUser_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        UserProfileUpdateDTO updateDTO = createUserProfileUpdateDTO(testUser.getUserId());

        mockMvc.perform(put("/api/users/profile/{userId}", testUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

}
