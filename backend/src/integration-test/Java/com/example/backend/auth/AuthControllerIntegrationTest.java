package com.example.backend.auth;

import com.example.backend.config.JwtTokenProvider;
import com.example.backend.dao.LoginLogsDao;
import com.example.backend.dao.UsersDao;
import com.example.backend.dto.auth.LoginDTO;
import com.example.backend.dto.auth.RegisterDTO;
import com.example.backend.dto.loginlogs.LoginLogVO;
import com.example.backend.entity.LoginLogs;
import com.example.backend.entity.Users;
import com.example.backend.service.UsersService;
import com.example.backend.service.impl.LoginLogsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;
import java.util.List;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@MapperScan("com.example.backend.dao")
public class AuthControllerIntegrationTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private LoginLogsDao loginLogsDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private LoginLogsServiceImpl loginLogsService;

    /**
     * 创建测试用户
     */
    private Users createTestUser() {
        Users user = new Users();
        user.setUserId(UUID.randomUUID().toString());
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
        return user;
    }
    private final String rawPassword = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testLogin_Success() throws Exception {
        Users user = createTestUser();
        usersService.registerUser(user, rawPassword);

        // 获取实际创建的用户ID
        Users createdUser = usersService.getUserByEmail(user.getEmail());
        assertNotNull(createdUser);
        String userId = createdUser.getUserId();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(user.getEmail());
        loginDTO.setPassword(rawPassword);

        // 执行登录请求
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .header("User-Agent", "Test-Agent/1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.role").value(user.getRole()));


        // 验证登录日志记录
        List<LoginLogVO> loginLogs = loginLogsService.getLoginLogsByUserId(userId);
        assertNotNull(loginLogs);
        assertFalse(loginLogs.isEmpty());

        LoginLogVO lastLoginLog = loginLogs.getFirst();
        assertNotNull(lastLoginLog);
        assertEquals(userId, lastLoginLog.getUserId());
        assertEquals("SUCCESS", lastLoginLog.getOperationResult());
        assertNotNull(lastLoginLog.getOperationTime());
        assertNotNull(lastLoginLog.getIpAddress());
        assertNotNull(lastLoginLog.getUserAgent());
        assertNotNull(lastLoginLog.getSessionId());
        assertNull(lastLoginLog.getDetails()); // 成功登录时失败原因为空
        }

    @Test
    public void testLogin_Failure_WrongPassword() throws Exception {
        // 先注册一个用户
        Users user = createTestUser();
        usersService.registerUser(user, rawPassword);

        // 获取实际创建的用户ID
        Users createdUser = usersService.getUserByEmail(user.getEmail());
        assertNotNull(createdUser);
        String userId = createdUser.getUserId();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(user.getEmail());
        loginDTO.setPassword("wrongpassword");

        // 执行登录请求
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .header("User-Agent", "Test-Agent/1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("パスワードが正しくありません"));

        // 验证失败登录日志记录
        List<LoginLogVO> loginLogs = loginLogsService.getLoginLogsByUserId(userId);
        assertNotNull(loginLogs);
        assertFalse(loginLogs.isEmpty());

        LoginLogVO failureLog = loginLogs.getFirst();
        assertNotNull(failureLog);
        assertEquals(userId, failureLog.getUserId());
        assertEquals("FAILURE", failureLog.getOperationResult());
        assertNotNull(failureLog.getDetails());
        assertEquals("パスワードが正しくありません", failureLog.getDetails());
        assertNotNull(failureLog.getOperationTime());
        assertNotNull(failureLog.getIpAddress());
        assertNotNull(failureLog.getUserAgent());
    }

    /**
     * 测试用户不存在时的登录失败日志记录
     */
    @Test
    public void testLogin_Failure_UserNotFound_WithLoginLog() throws Exception {
        String nonExistentEmail = "nonexistent@example.com";

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(nonExistentEmail);
        loginDTO.setPassword(rawPassword);

        // 执行登录请求
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .header("User-Agent", "Test-Agent/1.0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ユーザーが見つかりません"));

        // 验证登录日志记录（用户不存在时可能不会记录用户ID，但会记录登录邮箱）
        // 这里可以根据实际实现调整断言
        List<LoginLogs> allLogs = loginLogsDao.selectList(null); // 获取所有日志
        boolean foundFailureLog = allLogs.stream()
                .anyMatch(log -> nonExistentEmail.equals(log.getLoginEmail()) &&
                        "FAILURE".equals(log.getOperationResult()));
        assertTrue(foundFailureLog, "应该找到用户不存在的失败日志记录");
    }

    @Test
    public void testRegister_Success() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setFirstName("New");
        registerDTO.setLastName("User");
        registerDTO.setRole("User");
        registerDTO.setFirstKana("ニュー");
        registerDTO.setLastKana("ユーザー");
        registerDTO.setStatus("Active");
        registerDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("ユーザー登録が成功しました"));
    }

    @Test
    public void testRegister_Failure_DuplicateEmail() throws Exception {
        // 先注册一个相同邮箱的用户
        Users user = new Users();
        user.setEmail("duplicate@example.com");
        user.setFirstName("First");
        user.setLastName("User");
        user.setRole("User");
        user.setFirstKana("ファースト");
        user.setLastKana("ユーザー");
        user.setStatus("Active");
        usersService.registerUser(user, "password123");

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("duplicate@example.com");
        registerDTO.setFirstName("Second");
        registerDTO.setLastName("User");
        registerDTO.setRole("User");
        registerDTO.setFirstKana("セカンド");
        registerDTO.setLastKana("ユーザー");
        registerDTO.setStatus("Activate");
        registerDTO.setPassword("password123");

        //System.out.println(objectMapper.writeValueAsString(registerDTO));
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))

                .andExpect(status().isBadRequest());
    }
}