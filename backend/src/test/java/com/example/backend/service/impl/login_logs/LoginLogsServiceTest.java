package com.example.backend.service.impl.login_logs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.enums.OperationType;
import com.example.backend.dao.LoginLogsDao;
import com.example.backend.dto.loginlogs.LoginLogQueryDTO;
import com.example.backend.dto.loginlogs.LoginLogVO;
import com.example.backend.dto.loginlogs.UserLogDTO;
import com.example.backend.entity.LoginLogs;
import com.example.backend.service.impl.LoginLogsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginLogsServiceTest {
    @Mock
    private LoginLogsDao loginLogsDao;

    @InjectMocks
    private LoginLogsServiceImpl loginLogsService;

    private UserLogDTO userLogDTO;
    private LoginLogs loginLog;
    private final String testUserId = UUID.randomUUID().toString();
    private final String testLogId = "LOG_1234567890_abc123";

    private final Calendar calendar = Calendar.getInstance() ;
    private Date dateNow;
    private Date dateBefore;
    @BeforeEach
    void setUp() {
        dateNow = calendar.getTime();
        calendar.setTime(dateNow);
        calendar.add(Calendar.DATE, -1);
        dateBefore = calendar.getTime();

        userLogDTO = new UserLogDTO();
        userLogDTO.setUserId(testUserId);
        userLogDTO.setIpAddress("192.168.1.100");
        userLogDTO.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        userLogDTO.setSessionId("test-session-id");
        userLogDTO.setOperationType(OperationType.LOGIN);
        userLogDTO.setOperationResult("SUCCESS");
        userLogDTO.setOperationTime(dateNow);
        userLogDTO.setLoginEmail("test@example.com");

        loginLog = new LoginLogs();
        loginLog.setLogId(testLogId);
        loginLog.setUserId(testUserId);
        loginLog.setIpAddress("192.168.1.100");
        loginLog.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        loginLog.setSessionId("test-session-id");
        loginLog.setOperationType(OperationType.LOGIN);
        loginLog.setOperationResult("SUCCESS");
        loginLog.setOperationTime(dateNow);
        loginLog.setLoginEmail("test@example.com");
    }

    /**
     * 测试记录登录日志成功
     */
    @Test
    void testRecordLoginLog_Success() {
        // Given
        when(loginLogsDao.insert(any(LoginLogs.class))).thenReturn(1);

        // When
        loginLogsService.recordLoginLog(userLogDTO);

        // Then
        verify(loginLogsDao, times(1)).insert(any(LoginLogs.class));
    }

    /**
     * 测试记录登录日志 - 空DTO
     */
    @Test
    void testRecordLoginLog_NullDTO() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginLogsService.recordLoginLog(null);
        });
        assertEquals("UserLogDTOが見つかりません", exception.getMessage());
    }

    /**
     * 测试根据用户ID查询登录日志 - 有记录
     */
    @Test
    void testSelectByUserId_WithRecords() {
        // Given
        List<LoginLogs> mockLogs = Arrays.asList(loginLog);
        when(loginLogsDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(mockLogs);

        // When
        List<LoginLogVO> result = loginLogsService.getLoginLogsByUserId(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        LoginLogVO vo = result.get(0);
        assertEquals(testLogId, vo.getLogId());
        assertEquals(testUserId, vo.getUserId());
        assertEquals("SUCCESS", vo.getOperationResult());

        verify(loginLogsDao, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试根据用户ID查询登录日志 - 无记录
     */
    @Test
    void testSelectByUserId_NoRecords() {
        // Given
        when(loginLogsDao.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // When
        List<LoginLogVO> result = loginLogsService.getLoginLogsByUserId(testUserId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(loginLogsDao, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试根据用户ID查询登录日志 - 空用户ID
     */
    @Test
    void testSelectByUserId_NullUserId() {
        // When
        List<LoginLogVO> result = loginLogsService.getLoginLogsByUserId(null);

        // Then
        assertNotNull(result);
        verify(loginLogsDao, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试分页查询登录日志 - 所有条件
     */
    @Test
    void testGetLoginLogsPage_WithAllConditions() {
        // Given
        LoginLogQueryDTO queryDTO = new LoginLogQueryDTO();
        queryDTO.setUserId(testUserId);
        queryDTO.setOperationResult("SUCCESS");

        queryDTO.setStartTime(dateBefore);
        queryDTO.setEndTime(dateNow);
        queryDTO.setCurrent(1L);
        queryDTO.setSize(10L);

        Page<LoginLogs> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLog));
        mockPage.setTotal(1);

        when(loginLogsDao.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // When
        Page<LoginLogVO> result = loginLogsService.getLoginLogsPage(queryDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getTotal());

        LoginLogVO vo = result.getRecords().get(0);
        assertEquals(testLogId, vo.getLogId());
        assertEquals(testUserId, vo.getUserId());

        verify(loginLogsDao, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    /**
     * 测试分页查询登录日志 - 无条件
     */
    @Test
    void testGetLoginLogsPage_NoConditions() {
        // Given
        LoginLogQueryDTO queryDTO = new LoginLogQueryDTO();
        queryDTO.setCurrent(1L);
        queryDTO.setSize(10L);

        Page<LoginLogs> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLog, loginLog));
        mockPage.setTotal(2);

        when(loginLogsDao.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // When
        Page<LoginLogVO> result = loginLogsService.getLoginLogsPage(queryDTO);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(2, result.getTotal());
        verify(loginLogsDao, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    /**
     * 测试分页查询登录日志 - 部分条件
     */
    @Test
    void testGetLoginLogsPage_PartialConditions() {
        // Given
        LoginLogQueryDTO queryDTO = new LoginLogQueryDTO();
        queryDTO.setUserId(testUserId);
        queryDTO.setCurrent(1L);
        queryDTO.setSize(10L);

        Page<LoginLogs> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(loginLog));
        mockPage.setTotal(1);

        when(loginLogsDao.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // When
        Page<LoginLogVO> result = loginLogsService.getLoginLogsPage(queryDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(loginLogsDao, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    /**
     * 测试获取用户最近一次成功登录记录
     */
    @Test
    void testGetLastLoginLog_Success() {
        // Given
        when(loginLogsDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginLog);

        // When
        LoginLogVO result = loginLogsService.getLastLoginLog(testUserId, "SUCCESS");

        // Then
        assertNotNull(result);
        assertEquals(testLogId, result.getLogId());
        assertEquals(testUserId, result.getUserId());
        assertEquals("SUCCESS", result.getOperationResult());

        verify(loginLogsDao, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试获取用户最近一次失败登录记录
     */
    @Test
    void testGetLastLoginLog_Failure() {
        // Given
        LoginLogs failureLog = new LoginLogs();
        failureLog.setLogId("LOG_FAILURE_123");
        failureLog.setUserId(testUserId);
        failureLog.setOperationResult("FAILURE");
        failureLog.setDetails("密码错误");

        when(loginLogsDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(failureLog);

        // When
        LoginLogVO result = loginLogsService.getLastLoginLog(testUserId, "FAILURE");

        // Then
        assertNotNull(result);
        assertEquals("FAILURE", result.getOperationResult());
        assertEquals("密码错误", result.getDetails());

        verify(loginLogsDao, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试获取用户最近一次登录记录 - 无记录
     */
    @Test
    void testGetLastLoginLog_NoRecord() {
        // Given
        when(loginLogsDao.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When
        LoginLogVO result = loginLogsService.getLastLoginLog(testUserId, "SUCCESS");

        // Then
        assertNull(result);
        verify(loginLogsDao, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试获取用户最近一次登录记录 - 空用户ID
     */
    @Test
    void testGetLastLoginLog_NullUserId() {
        // When
        LoginLogVO result = loginLogsService.getLastLoginLog(null, "SUCCESS");

        // Then
        assertNull(result);
        verify(loginLogsDao, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试获取用户最近一次登录记录 - 空操作结果
     */
    @Test
    void testGetLastLoginLog_NullOperationResult() {
        // When
        LoginLogVO result = loginLogsService.getLastLoginLog(testUserId, null);

        // Then
        assertNull(result);
        verify(loginLogsDao, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试记录登录日志 - 数据库插入失败
     */
    @Test
    void testRecordLoginLog_DatabaseFailure() {
        // Given
        when(loginLogsDao.insert(any(LoginLogs.class))).thenReturn(0);

        // When & Then
        assertDoesNotThrow(() -> loginLogsService.recordLoginLog(userLogDTO));
        verify(loginLogsDao, times(1)).insert(any(LoginLogs.class));
    }

    /**
     * 测试分页查询 - 空结果
     */
    @Test
    void testGetLoginLogsPage_EmptyResult() {
        // Given
        LoginLogQueryDTO queryDTO = new LoginLogQueryDTO();
        queryDTO.setCurrent(1L);
        queryDTO.setSize(10L);

        Page<LoginLogs> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList());
        mockPage.setTotal(0);

        when(loginLogsDao.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // When
        Page<LoginLogVO> result = loginLogsService.getLoginLogsPage(queryDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
        assertEquals(0, result.getTotal());
        verify(loginLogsDao, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }
}
