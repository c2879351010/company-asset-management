package com.example.backend.controller;

import com.example.backend.config.JwtTokenProvider;
import com.example.backend.dao.UsersDao;
import com.example.backend.dto.auth.ChangePasswordDTO;
import com.example.backend.dto.users.UserProfileUpdateDTO;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserProfileControllerTest {

    @Mock
    private UsersService mockUsersService;

    @InjectMocks
    private UserProfileController userProfileController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userProfileController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void updateUser_success_returnsOk() throws Exception {
        UserUpdateVO vo = new UserUpdateVO();
        vo.setUserId("u1");
        when(mockUsersService.updateUser(any(UserProfileUpdateDTO.class))).thenReturn(vo);

        UserProfileUpdateDTO req = new UserProfileUpdateDTO();
        req.setUserId("u1");
        req.setEmail("different@email.com");
        req.setFirstKana("Different");
        req.setLastKana("Different");
        req.setFirstName("Different");
        req.setLastName("Different");

        mockMvc.perform(put("/api/users/profile/{userId}", "u1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("u1")));
    }

    @Test
    void updateUser_mismatchedUserId_returnsBadRequest() throws Exception {
        UserProfileUpdateDTO req = new UserProfileUpdateDTO();
        req.setUserId("different");

        mockMvc.perform(put("/api/users/profile/{userId}", "u1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_success_returnsOkMessage() throws Exception {
        // 默认不抛异常，表示成功
        ChangePasswordDTO req = new ChangePasswordDTO();
        req.setUserId("u1");
        req.setOldPassword("old");
        req.setNewPassword("new");

        mockMvc.perform(put("/api/users/profile/change-password/{userId}", "u1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Password changed successfully")));
    }

    @Test
    void changePassword_serviceThrows_returnsBadRequestWithMessage() throws Exception {
        doThrow(new RuntimeException("invalid old password"))
                .when(mockUsersService).changePassword(any(ChangePasswordDTO.class));

        ChangePasswordDTO req = new ChangePasswordDTO();
        req.setUserId("u1");
        req.setOldPassword("bad");
        req.setNewPassword("new");

        mockMvc.perform(put("/api/users/profile/change-password/{userId}", "u1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("invalid old password")));
    }
    @Test
    void updateUser_requestBodyMissingUserId_returnsBadRequest() throws Exception {
        // 请求体中没有 userId，path userId 有值 -> 触发 request.getUserId() == null 分支
        UserProfileUpdateDTO req = new UserProfileUpdateDTO(); // userId 未设置 (null)

        mockMvc.perform(put("/api/users/profile/{userId}", "u1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_nullPathUserId_directCall_returnsBadRequest() {
        // 通过直接调用控制器并传入 null path 参数，触发 userId == null 分支
        UserProfileUpdateDTO req = new UserProfileUpdateDTO();
        ResponseEntity<UserUpdateVO> resp = userProfileController.UpdateUserByUser(null, req);
        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void changePassword_requestBodyMissingUserId_returnsBadRequest() throws Exception {
        // 请求体中没有 userId，path userId 有值 -> 触发 request.getUserId() == null 分支
        ChangePasswordDTO req = new ChangePasswordDTO(); // userId 未设置

        mockMvc.perform(put("/api/users/profile/change-password/{userId}", "u1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_nullPathUserId_directCall_returnsBadRequest() {
        // 直接调用 controller，path userId 为 null -> 触发 userId == null 分支
        ChangePasswordDTO req = new ChangePasswordDTO();
        ResponseEntity<String> resp = userProfileController.ChangePasswordByUser(null, req);
        assertEquals(400, resp.getStatusCodeValue());
    }
}
