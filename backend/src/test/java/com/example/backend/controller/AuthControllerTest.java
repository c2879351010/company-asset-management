package com.example.backend.controller;

import com.example.backend.dto.auth.LoginDTO;
import com.example.backend.dto.auth.RegisterDTO;
import com.example.backend.entity.Users;
import com.example.backend.service.LoginLogsService;
import com.example.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private UsersService usersService;

    @Mock
    private LoginLogsService loginLogsService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void login_success_recordsSuccessLog_and_returnsTokenAndUser() throws Exception {
        String email = "u@example.com";
        String rawPassword = "pass";
        String token = "jwt-token";
        Users user = new Users();
        user.setUserId("uid-1");
        user.setEmail(email);
        user.setFirstName("FN");
        user.setLastName("LN");
        user.setRole("User");

        when(usersService.isEmailExists(email)).thenReturn(true);
        when(usersService.getUserByEmail(email)).thenReturn(user);
        when(usersService.authenticateUser(email, rawPassword)).thenReturn(token);

        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword(rawPassword);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("User-Agent", "UA/1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.userId").value(user.getUserId()));

        verify(loginLogsService, times(1)).recordLoginLog(any());
    }

    @Test
    void login_failure_wrongPassword_returnsBadRequest_and_recordsFailure() throws Exception {
        String email = "u2@example.com";
        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword("bad");

        when(usersService.isEmailExists(email)).thenReturn(true);
        when(usersService.getUserByEmail(email)).thenReturn(new Users());
        when(usersService.authenticateUser(email, dto.getPassword()))
                .thenThrow(new RuntimeException("パスワードが正しくありません"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("User-Agent", "UA/1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("パスワードが正しくありません"));

        verify(loginLogsService, times(1)).recordLoginLog(any());
    }

    @Test
    void login_failure_userNotFound_returnsBadRequest_and_recordsFailure() throws Exception {
        String email = "notfound@example.com";
        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword("p");

        when(usersService.isEmailExists(email)).thenReturn(false);
        when(usersService.authenticateUser(email, dto.getPassword()))
                .thenThrow(new RuntimeException("ユーザーが見つかりません"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("User-Agent", "UA/1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("ユーザーが見つかりません"));

        verify(loginLogsService, times(1)).recordLoginLog(any());
    }

    @Test
    void register_success_returnsOkMessage() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("new@example.com");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setRole("User");
        dto.setFirstKana("ファ");
        dto.setLastKana("カ");
        dto.setStatus("Active");
        dto.setPassword("pwd");

        when(usersService.registerUser(any(Users.class), eq("pwd"))).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("ユーザー登録が成功しました"));
    }

    @Test
    void register_returnsBadRequest_whenRegisterReturnsFalse() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("fail@example.com");
        dto.setPassword("pwd2");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setRole("User");
        dto.setFirstKana("ファ");
        dto.setLastKana("カ");
        dto.setStatus("Active");
        when(usersService.registerUser(any(Users.class), eq("pwd2"))).thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("ユーザー登録に失敗しました"));
    }

    @Test
    void register_returnsBadRequest_whenServiceThrowsException() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("err@example.com");
        dto.setPassword("pwd3");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setRole("User");
        dto.setFirstKana("ファ");
        dto.setLastKana("カ");
        dto.setStatus("Active");
        when(usersService.registerUser(any(Users.class), eq("pwd3")))
                .thenThrow(new RuntimeException("service error"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("service error"));
    }
}
