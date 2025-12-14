package com.example.backend.service.impl.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.config.JwtTokenProvider;
import com.example.backend.dao.UsersDao;
import com.example.backend.entity.Users;
import com.example.backend.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class LoginServiceTest {

    @Mock
    private UsersDao usersDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UsersServiceImpl usersServiceImpl;

    private Users testUser;
    private final String rawPassword = "password123";
    private final String hashedPassword = "$2a$10$hashedPassword";
    private final String testToken = "test.jwt.token";
    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setUserId(UUID.randomUUID().toString());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("太郎");
        testUser.setLastName("山田");
        testUser.setFirstKana("タロウ");
        testUser.setLastKana("ヤマダ");
        testUser.setPasswordHash(hashedPassword);
        testUser.setRole("ADMIN");
        testUser.setStatus("Active");
        testUser.setCreatedAt(new Date());
        testUser.setUpdatedAt(new Date());
    }

    /**
     * 测试用户认证成功
     */
    @Test
    public void testAuthenticateUser_Success() {
        // Given

        when(usersDao.selectOne(any(QueryWrapper.class))).thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtTokenProvider.generateToken(testUser)).thenReturn(testToken);

        // When
        String token = usersServiceImpl.authenticateUser("test@example.com", rawPassword);

        // Then
        assertNotNull(token, "认证成功应该返回token");
        assertEquals(testToken, token);

        verify(usersDao, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
        verify(jwtTokenProvider, times(1)).generateToken(testUser);
    }

    /**
     * 测试用户认证失败 - 用户不存在
     */
    @Test
    public void testAuthenticateUser_UserNotFound() {
        // Given
        when(usersDao.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersServiceImpl.authenticateUser("nonexistent@example.com", rawPassword);
        });

        assertEquals("ユーザーが見つかりません", exception.getMessage());
        verify(usersDao, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateToken(any(Users.class));
    }

    /**
     * 测试用户认证失败 - 密码错误
     */
    @Test
    public void testAuthenticateUser_WrongPassword() {
        // Given
        when(usersDao.selectOne(any(QueryWrapper.class))).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", hashedPassword)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersServiceImpl.authenticateUser("test@example.com", "wrongpassword");
        });

        assertEquals("パスワードが正しくありません", exception.getMessage());
        verify(usersDao, times(1)).selectOne(any(QueryWrapper.class));
        verify(passwordEncoder, times(1)).matches("wrongpassword", hashedPassword);
        verify(jwtTokenProvider, never()).generateToken(any(Users.class));
    }

    /**
     * 测试根据邮箱获取用户
     */
    @Test
    public void testGetUserByEmail_Success() {
        // Given
        when(usersDao.selectOne(any(QueryWrapper.class))).thenReturn(testUser);

        // When
        Users foundUser = usersServiceImpl.getUserByEmail("test@example.com");

        // Then
        assertNotNull(foundUser, "应该能找到用户");
        assertEquals(testUser.getUserId(), foundUser.getUserId());
        assertEquals("太郎", foundUser.getFirstName());
        assertEquals("山田", foundUser.getLastName());
        assertEquals("ADMIN", foundUser.getRole());

        verify(usersDao, times(1)).selectOne(any(QueryWrapper.class));
    }

    /**
     * 测试根据邮箱获取用户 - 用户不存在
     */
    @Test
    public void testGetUserByEmail_NotFound() {
        // Given
        when(usersDao.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When
        Users foundUser = usersServiceImpl.getUserByEmail("notfound@example.com");

        // Then
        assertNull(foundUser, "应该返回null");
        verify(usersDao, times(1)).selectOne(any(QueryWrapper.class));
    }

    /**
     * 测试根据用户ID获取用户
     */
    @Test
    public void testGetUserById_Success() {
        // Given
        String userId = testUser.getUserId();
        when(usersDao.selectById(userId)).thenReturn(testUser);

        // When
        Users foundUser = usersServiceImpl.getUserById(userId);

        // Then
        assertNotNull(foundUser, "应该能找到用户");
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals("太郎", foundUser.getFirstName());

        verify(usersDao, times(1)).selectById(userId);
    }

    /**
     * 测试根据用户ID获取用户 - 用户不存在
     */
    @Test
    public void testGetUserById_NotFound() {
        // Given
        String nonExistentUserId = "non-existent-id";
        when(usersDao.selectById(nonExistentUserId)).thenReturn(null);

        // When
        Users foundUser = usersServiceImpl.getUserById(nonExistentUserId);

        // Then
        assertNull(foundUser, "应该返回null");
        verify(usersDao, times(1)).selectById(nonExistentUserId);
    }

}
