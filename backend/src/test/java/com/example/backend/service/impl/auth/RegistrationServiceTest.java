package com.example.backend.service.impl.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class RegistrationServiceTest {

    @Mock
    private UsersDao usersDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl usersService;

    private Users testUser;
    private final String rawPassword = "password123";
    private final String hashedPassword = "$2a$10$hashedPassword";

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("太郎");
        testUser.setLastName("山田");
        testUser.setFirstKana("タロウ");
        testUser.setLastKana("ヤマダ");
        testUser.setRole("ADMIN");
        testUser.setStatus("Active");
    }

    @Test
    public void testRegisterUser_Success() {
        // When
        when(usersDao.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(usersDao.insert(any(Users.class))).thenReturn(1);


        boolean result = usersService.registerUser(testUser, rawPassword);

        // Then
        assertTrue(result);
        assertNotNull(testUser.getUserId());
        assertEquals(hashedPassword, testUser.getPasswordHash());
        assertNotNull(testUser.getCreatedAt());
        assertNotNull(testUser.getUpdatedAt());

        verify(usersDao, times(1)).selectCount(any(QueryWrapper.class));
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(usersDao, times(1)).insert(testUser);
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        // Given
        Users existingUser = new Users();
        when(usersDao.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.registerUser(testUser, rawPassword);
        });

        assertEquals("メールアドレスは既に使用されています", exception.getMessage());

        verify(usersDao, times(1)).selectCount(any(QueryWrapper.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(usersDao, never()).insert(any(Users.class));
    }


    @Test
    public void testRegisterUser_PasswordHashing() {
        // Given
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(usersDao.insert(any(Users.class))).thenReturn(1);

        // When
        boolean result = usersService.registerUser(testUser, rawPassword);

        // Then
        assertTrue(result);
        assertNotEquals(rawPassword, testUser.getPasswordHash());
        assertEquals(hashedPassword, testUser.getPasswordHash());

        verify(passwordEncoder, times(1)).encode(rawPassword);
    }

    @Test
    public void testRegisterUser_InsertFails() {
        // When
        when(usersDao.insert(any(Users.class))).thenReturn(0); // 插入失败


        boolean result = usersService.registerUser(testUser, rawPassword);

        // Then
        assertFalse(result);
        verify(usersDao, times(1)).insert(testUser);
    }

    @Test
    public void testRegisterUser_AllFieldsPersisted() {
        // Given
        when(usersDao.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(usersDao.insert(any(Users.class))).thenReturn(1);
        // When
        boolean result = usersService.registerUser(testUser, rawPassword);

        // Then
        assertTrue(result);
        assertEquals("太郎", testUser.getFirstName());
        assertEquals("山田", testUser.getLastName());
        assertEquals("タロウ", testUser.getFirstKana());
        assertEquals("ヤマダ", testUser.getLastKana());
        assertEquals("ADMIN", testUser.getRole());
        assertEquals("test@example.com", testUser.getEmail());

        verify(usersDao, times(1)).insert(testUser);
    }


    @Test
    void registerUser_nullUser_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usersService.registerUser(null, rawPassword));
        assertEquals("ユーザー情報またはパスワードが無効です", ex.getMessage());
        verify(usersDao, never()).insert((Users) any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void registerUser_nullPassword_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usersService.registerUser(testUser, null));
        assertEquals("ユーザー情報またはパスワードが無効です", ex.getMessage());
        verify(usersDao, never()).insert((Users) any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void registerUser_emptyPassword_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> usersService.registerUser(testUser, ""));
        assertEquals("ユーザー情報またはパスワードが無効です", ex.getMessage());
        verify(usersDao, never()).insert((Users) any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void registerUser_emailAlreadyExists_throwsRuntimeException() {
        when(usersDao.selectCount(any())).thenReturn(1L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usersService.registerUser(testUser, rawPassword));
        assertEquals("メールアドレスは既に使用されています", ex.getMessage());

        verify(usersDao, times(1)).selectCount(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usersDao, never()).insert((Users) any());
    }

    @Test
    void registerUser_success_returnsTrue_and_populatesFields() {
        when(usersDao.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(usersDao.insert(any(Users.class))).thenReturn(1);

        boolean result = usersService.registerUser(testUser, rawPassword);

        assertTrue(result);
        assertNotNull(testUser.getUserId());
        assertEquals(hashedPassword, testUser.getPasswordHash());
        assertNotNull(testUser.getCreatedAt());
        assertNotNull(testUser.getUpdatedAt());
        // createdAt/updatedAt should be recent; at least instances of java.util.Date
        assertTrue(testUser.getCreatedAt().before(Calendar.getInstance().getTime()) ||
                testUser.getCreatedAt().equals(Calendar.getInstance().getTime()));

        verify(usersDao, times(1)).selectCount(any());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(usersDao, times(1)).insert(testUser);
    }

    @Test
    void registerUser_insertFails_returnsFalse() {
        when(usersDao.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        when(usersDao.insert(any(Users.class))).thenReturn(0);

        boolean result = usersService.registerUser(testUser, rawPassword);

        assertFalse(result);
        verify(usersDao, times(1)).selectCount(any());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(usersDao, times(1)).insert(testUser);
    }
}
