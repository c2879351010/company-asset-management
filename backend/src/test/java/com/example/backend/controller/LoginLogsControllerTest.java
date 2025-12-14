// java
package com.example.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.dto.loginlogs.LoginLogQueryDTO;
import com.example.backend.dto.loginlogs.LoginLogVO;
import com.example.backend.service.LoginLogsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class LoginLogsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginLogsService loginLogService;

    @InjectMocks
    private LoginLogsController loginLogsController;

    private ObjectMapper objectMapper;
    private LoginLogVO loginLogVO;
    private final String testUserId = UUID.randomUUID().toString();
    private LoginLogQueryDTO queryDTO;
    private Calendar calendar = Calendar.getInstance();
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginLogsController).build();
        objectMapper = new ObjectMapper();

        loginLogVO = new LoginLogVO();
        loginLogVO.setLogId("LOG_123456");
        loginLogVO.setUserId(testUserId);
        loginLogVO.setLoginEmail("test@example.com");
        loginLogVO.setOperationResult("SUCCESS");
        loginLogVO.setOperationTime(calendar.getTime());
        loginLogVO.setIpAddress("192.168.1.100");
        loginLogVO.setUserAgent("Test-Agent");

        queryDTO = new LoginLogQueryDTO();
        queryDTO.setUserId(testUserId);
        queryDTO.setCurrent(1L);
        queryDTO.setSize(10L);
    }

    /**
     * 测试根据用户ID查询登录日志 - 成功
     */
    @Test
    void testGetLoginLogsByUserId_Success() throws Exception {
        // Given
        List<LoginLogVO> mockLogs = Arrays.asList(loginLogVO, loginLogVO);
        when(loginLogService.getLoginLogsByUserId(testUserId)).thenReturn(mockLogs);

        // When & Then
        mockMvc.perform(get("/api/login-logs/user/{userId}", testUserId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(testUserId))
                .andExpect(jsonPath("$[0].operationResult").value("SUCCESS"))
                .andExpect(jsonPath("$[1].userId").value(testUserId));

        verify(loginLogService, times(1)).getLoginLogsByUserId(testUserId);
    }

    /**
     * 测试根据用户ID查询登录日志 - 空结果
     */
    @Test
    void testGetLoginLogsByUserId_EmptyList() throws Exception {
        // Given
        when(loginLogService.getLoginLogsByUserId(testUserId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/login-logs/user/{userId}", testUserId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(loginLogService, times(1)).getLoginLogsByUserId(testUserId);
    }

    /**
     * 测试根据用户ID查询登录日志 - 服务层异常
     */
    @Test
    void testGetLoginLogsByUserId_ServiceException() throws Exception {
        // Given
        when(loginLogService.getLoginLogsByUserId(testUserId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/login-logs/user/{userId}", testUserId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(loginLogService, times(1)).getLoginLogsByUserId(testUserId);
    }

/*    *//**
     * 测试根据用户ID查询登录日志 - 空用户ID
     *//*
    @Test
    void testGetLoginLogsByUserId_NullUserId() throws Exception {
        // Given
        when(loginLogService.getLoginLogsByUserId(null)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/login-logs/user/{userId}", "null")
                        .contentType("application/json"))
                .andExpect(status().bad)
                .andExpect(jsonPath("$.length()").value(0));

        verify(loginLogService, times(1)).getLoginLogsByUserId(null);
    }*/

    /**
     * 测试获取用户最近一次登录记录 - 成功
     */
    @Test
    void testGetLastLoginLog_Success() throws Exception {
        // Given
        when(loginLogService.getLastLoginLog(eq(testUserId), eq("SUCCESS"))).thenReturn(loginLogVO);

        // When & Then
        mockMvc.perform(get("/api/login-logs/last-login/{userId}", testUserId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.operationResult").value("SUCCESS"))
                .andExpect(jsonPath("$.logId").value("LOG_123456"))
                .andExpect(jsonPath("$.loginEmail").value("test@example.com"));

        verify(loginLogService, times(1)).getLastLoginLog(testUserId, "SUCCESS");
    }

    /**
     * 测试获取用户最近一次登录记录 - 无记录
     */
    @Test
    void testGetLastLoginLog_NoRecord() throws Exception {
        // Given
        when(loginLogService.getLastLoginLog(eq(testUserId), eq("SUCCESS"))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/login-logs/last-login/{userId}", testUserId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(loginLogService, times(1)).getLastLoginLog(testUserId, "SUCCESS");
    }

    /**
     * 测试获取用户最近一次登录记录 - 服务层异常
     */
    @Test
    void testGetLastLoginLog_ServiceException() throws Exception {
        // Given
        when(loginLogService.getLastLoginLog(eq(testUserId), eq("SUCCESS")))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/login-logs/last-login/{userId}", testUserId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(loginLogService, times(1)).getLastLoginLog(testUserId, "SUCCESS");
    }

/*
    */
/**
     * 测试获取用户最近一次登录记录 - 空用户ID
     *//*

    @Test
    void testGetLastLoginLog_NullUserId() throws Exception {
        // Given
        when(loginLogService.getLastLoginLog(eq(null), eq("SUCCESS"))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/login-logs/last-login/{userId}", "null")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(loginLogService, times(1)).getLastLoginLog(null, "SUCCESS");
    }
*/

    /**
     * 测试分页查询登录日志 - 成功
     */
    @Test
    void testGetLoginLogsPage_Success() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLogVO, loginLogVO));
        mockPage.setTotal(2);

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("current", "1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(2))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.current").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.records[0].userId").value(testUserId));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 空结果
     */
    @Test
    void testGetLoginLogsPage_EmptyResult() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Collections.emptyList());
        mockPage.setTotal(0);

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("current", "1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(0))
                .andExpect(jsonPath("$.total").value(0));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 服务层异常
     */
    @Test
    void testGetLoginLogsPage_ServiceException() throws Exception {
        // Given
        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class)))
                .thenThrow(new RuntimeException("Pagination error"));

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("current", "1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 无查询参数
     */
    @Test
    void testGetLoginLogsPage_NoParameters() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLogVO));
        mockPage.setTotal(1);

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 单条记录
     */
    @Test
    void testGetLoginLogsPage_SingleRecord() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLogVO));
        mockPage.setTotal(1);

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("current", "1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.records[0].userId").value(testUserId));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 多页数据
     */
    @Test
    void testGetLoginLogsPage_MultiplePages() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(2, 5);
        List<LoginLogVO> records = Arrays.asList(loginLogVO, loginLogVO, loginLogVO, loginLogVO, loginLogVO);
        mockPage.setRecords(records);
        mockPage.setTotal(15);

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("current", "2")
                        .param("size", "5")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(5))
                .andExpect(jsonPath("$.total").value(15))
                .andExpect(jsonPath("$.current").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.records[0].userId").value(testUserId));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 带操作结果筛选
     */
    @Test
    void testGetLoginLogsPage_WithOperationResult() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLogVO));
        mockPage.setTotal(1);

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .param("userId", testUserId)
                        .param("operationResult", "SUCCESS")
                        .param("current", "1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(1))
                .andExpect(jsonPath("$.records[0].operationResult").value("SUCCESS"));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试分页查询登录日志 - 带时间范围筛选
     */
    @Test
    void testGetLoginLogsPage_WithTimeRange() throws Exception {
        // Given
        Page<LoginLogVO> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLogVO));
        mockPage.setTotal(1);
        LoginLogQueryDTO loginLogQueryDTO = new LoginLogQueryDTO();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        loginLogQueryDTO.setStartTime(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        loginLogQueryDTO.setEndTime(calendar.getTime());

        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get("/api/login-logs/page")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginLogQueryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records.length()").value(1));

        verify(loginLogService, times(1)).getLoginLogsPage(any(LoginLogQueryDTO.class));
    }

    /**
     * 测试所有端点URL路径
     */
    @Test
    void testAllEndpoints_UrlPaths() throws Exception {
        // 测试/user/{userId}端点
        when(loginLogService.getLoginLogsByUserId(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/login-logs/user/123"))
                .andExpect(status().isOk());

        // 测试/last-login/{userId}端点
        when(loginLogService.getLastLoginLog(anyString(), anyString())).thenReturn(null);
        mockMvc.perform(get("/api/login-logs/last-login/123"))
                .andExpect(status().isOk());

        // 测试/page端点
        when(loginLogService.getLoginLogsPage(any(LoginLogQueryDTO.class))).thenReturn(new Page<>());
        mockMvc.perform(get("/api/login-logs/page"))
                .andExpect(status().isOk());
    }
}
