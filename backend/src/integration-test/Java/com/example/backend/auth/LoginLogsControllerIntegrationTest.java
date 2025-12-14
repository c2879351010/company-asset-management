package com.example.backend.auth;
import com.example.backend.common.enums.OperationType;
import com.example.backend.dto.loginlogs.LoginLogQueryDTO;
import com.example.backend.dto.loginlogs.UserLogDTO;
import com.example.backend.entity.Users;
import com.example.backend.service.LoginLogsService;
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
public class LoginLogsControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginLogsService loginLogsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersService usersService;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    private String testUserId;
    private String testLogId;
    private String rawPassword = "Test@1234";
    @BeforeEach
    void setUp() {
        Users user = new Users();
        user.setUserId(testUserId);
        user.setEmail("test@example.com");
        user.setFirstName("太郎");
        user.setLastName("山田");
        user.setFirstKana("タロウ");
        user.setLastKana("ヤマダ");
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole("ADMIN");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus("Active");
        usersService.registerUser(user, rawPassword);
        usersService.getUserByEmail("test@example.com");
        testUserId = user.getUserId();


        // 插入一条测试登录日志
        UserLogDTO userLogDTO = new UserLogDTO();
        testLogId = "log-id-123";


        userLogDTO.setUserId(testUserId);
        userLogDTO.setIpAddress("192.168.1.1");
        userLogDTO.setUserAgent("Mozilla/5.0...");
        userLogDTO.setSessionId("session-123");
        userLogDTO.setOperationType(OperationType.LOGIN);
        userLogDTO.setOperationTime(new Date());
        userLogDTO.setLoginEmail("test@example.com");
        userLogDTO.setOperationResult("SUCCESS");
        userLogDTO.setDetails("Login successful");


        loginLogsService.recordLoginLog(userLogDTO);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getLoginLogsByUserId_ShouldReturnLogs() throws Exception {
        mockMvc.perform(get("/api/login-logs/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(testUserId));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getLastLoginLog_ShouldReturnLastSuccessLog() throws Exception {
        mockMvc.perform(get("/api/login-logs/last-login/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.operationResult").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getLoginLogsPage_ShouldReturnPaginatedLogs() throws Exception {
        LoginLogQueryDTO queryDTO = new LoginLogQueryDTO();
        queryDTO.setUserId(testUserId);
        queryDTO.setCurrent(1L);
        queryDTO.setSize(10L);

        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records[0].userId").value(testUserId));
    }

}
