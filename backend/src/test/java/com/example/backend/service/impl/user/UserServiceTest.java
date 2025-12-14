package com.example.backend.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.dao.UsersDao;
import com.example.backend.dto.auth.ChangePasswordDTO;
import com.example.backend.dto.users.AdminUserUpdateDTO;
import com.example.backend.dto.users.UserProfileUpdateDTO;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.entity.Users;
import com.example.backend.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @InjectMocks
    private UsersServiceImpl usersService;
    
    @Mock
    private UsersDao usersDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Users existingUser;
    private Users userToUpdate;
    private AdminUserUpdateDTO adminUserUpdateDTO;
    private UserProfileUpdateDTO userProfileUpdateDTO;
    private ChangePasswordDTO changePasswordDTO;
    @BeforeEach
    void setUp() {
        existingUser = new Users();
        existingUser.setUserId(UUID.randomUUID().toString());
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("太郎");
        existingUser.setLastName("山田");
        existingUser.setFirstKana("タロウ");
        existingUser.setLastKana("ヤマダ");
        existingUser.setPasswordHash("hashedPassword");
        existingUser.setRole("Admin");
        existingUser.setStatus("Active");
        existingUser.setCreatedAt(new Date());
        existingUser.setUpdatedAt(new Date());

        adminUserUpdateDTO = new AdminUserUpdateDTO();
        adminUserUpdateDTO.setUserId(existingUser.getUserId());
        adminUserUpdateDTO.setFirstName("次郎");
        adminUserUpdateDTO.setLastName("鈴木");
        adminUserUpdateDTO.setFirstKana("ジロウ");
        adminUserUpdateDTO.setLastKana("スズキ");
        adminUserUpdateDTO.setRole("User");
        adminUserUpdateDTO.setStatus("Inactive");
        adminUserUpdateDTO.setEmail("new@example.com");

        userProfileUpdateDTO = new UserProfileUpdateDTO();
        userProfileUpdateDTO.setUserId(existingUser.getUserId());
        userProfileUpdateDTO.setFirstName("次郎");
        userProfileUpdateDTO.setLastName("鈴木");
        userProfileUpdateDTO.setFirstKana("ジロウ");
        userProfileUpdateDTO.setLastKana("スズキ");
        userProfileUpdateDTO.setEmail("new@example.com");

        userToUpdate = new Users();
        userToUpdate.setUserId(existingUser.getUserId());
        userToUpdate.setEmail(adminUserUpdateDTO.getEmail());
        userToUpdate.setFirstName(adminUserUpdateDTO.getFirstName());
        userToUpdate.setLastName(adminUserUpdateDTO.getLastName());
        userToUpdate.setFirstKana(adminUserUpdateDTO.getFirstKana());
        userToUpdate.setLastKana(adminUserUpdateDTO.getLastKana());
        userToUpdate.setRole(adminUserUpdateDTO.getRole());
        userToUpdate.setStatus(adminUserUpdateDTO.getStatus());
        userToUpdate.setPasswordHash(existingUser.getPasswordHash());
        userToUpdate.setCreatedAt(existingUser.getCreatedAt());
        userToUpdate.setUpdatedAt(new Date());

        changePasswordDTO = new ChangePasswordDTO( );
        changePasswordDTO.setUserId(existingUser.getUserId());
        changePasswordDTO.setOldPassword("hashedPassword");
        changePasswordDTO.setNewPassword("newPassword");
    }

    @Test
    public void testUpdateUserAdmin_Success() {
        when(usersDao.selectById(adminUserUpdateDTO.getUserId())).thenReturn(existingUser);
        when(usersDao.updateById(any(Users.class))).thenReturn(1);

        //Then
        UserUpdateVO response = usersService.updateUser(adminUserUpdateDTO);
        assertNotNull(response);
        assertEquals(userToUpdate.getUserId(), response.getUserId());
        assertEquals(userToUpdate.getLastName(), response.getLastName());
        assertEquals(userToUpdate.getFirstName(), response.getFirstName());
        assertEquals(userToUpdate.getLastKana(), response.getLastKana());
        assertEquals(userToUpdate.getFirstKana(), response.getFirstKana());
        assertEquals(userToUpdate.getEmail(), response.getEmail());
        assertEquals(userToUpdate.getStatus(), response.getStatus());
        assertEquals(userToUpdate.getRole(), response.getRole());

        verify(usersDao,times(1)).selectById(adminUserUpdateDTO.getUserId());
        verify(usersDao,times(1)).updateById(any(Users.class));
    }

    @Test
    void testUpdateUserAdmin_UserNotFound() {
        // 模拟用户不存在
        when(usersDao.selectById(adminUserUpdateDTO.getUserId())).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.updateUser(adminUserUpdateDTO));

        assertEquals("ユーザーが見つかりません", exception.getMessage());

        // 验证交互
        verify(usersDao, times(1)).selectById(adminUserUpdateDTO.getUserId());
        verify(usersDao, never()).updateById(any(Users.class));
    }

    @Test
    void testUpdateUserAdmin_UpdateFailed() {
        // 模拟用户存在但更新失败
        when(usersDao.selectById(adminUserUpdateDTO.getUserId())).thenReturn(existingUser);
        when(usersDao.updateById(any(Users.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.updateUser(adminUserUpdateDTO));

        assertEquals("ユーザーの更新に失敗しました", exception.getMessage());

        // 验证交互
        verify(usersDao, times(1)).selectById(adminUserUpdateDTO.getUserId());
        verify(usersDao, times(1)).updateById(any(Users.class));
    }
    @Test
    public void testUpdateUserProfile_Success() {
        when(usersDao.selectById(userProfileUpdateDTO.getUserId())).thenReturn(existingUser);
        when(usersDao.updateById(any(Users.class))).thenReturn(1);

        //Then
        UserUpdateVO response = usersService.updateUser(userProfileUpdateDTO);
        assertNotNull(response);
        assertEquals(userToUpdate.getUserId(), response.getUserId());
        assertEquals(userToUpdate.getLastName(), response.getLastName());
        assertEquals(userToUpdate.getFirstName(), response.getFirstName());
        assertEquals(userToUpdate.getLastKana(), response.getLastKana());
        assertEquals(userToUpdate.getFirstKana(), response.getFirstKana());
        assertEquals(userToUpdate.getEmail(), response.getEmail());
        assertEquals(existingUser.getStatus(), response.getStatus());
        assertEquals(existingUser.getRole(), response.getRole());

        verify(usersDao,times(1)).selectById(userProfileUpdateDTO.getUserId());
        verify(usersDao,times(1)).updateById(any(Users.class));
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        // 模拟用户不存在
        when(usersDao.selectById(userProfileUpdateDTO.getUserId())).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.updateUser(userProfileUpdateDTO));

        assertEquals("ユーザーが見つかりません", exception.getMessage());

        // 验证交互
        verify(usersDao, times(1)).selectById(userProfileUpdateDTO.getUserId());
        verify(usersDao, never()).updateById(any(Users.class));
    }

    @Test
    void testUpdateUserProfile_UpdateFailed() {
        // 模拟用户存在但更新失败
        when(usersDao.selectById(userProfileUpdateDTO.getUserId())).thenReturn(existingUser);
        when(usersDao.updateById(any(Users.class))).thenReturn(0);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.updateUser(userProfileUpdateDTO));

        assertEquals("ユーザーの更新に失敗しました", exception.getMessage());

        // 验证交互
        verify(usersDao, times(1)).selectById(userProfileUpdateDTO.getUserId());
        verify(usersDao, times(1)).updateById(any(Users.class));
    }

    @Test
    void testChangePassword_Success() {
        // 模拟用户存在且旧密码匹配
        when(usersDao.selectById(changePasswordDTO.getUserId())).thenReturn(existingUser);
        when(usersDao.updateById(any(Users.class))).thenReturn(1);
        when(passwordEncoder.matches(changePasswordDTO.getOldPassword(), existingUser.getPasswordHash()))
                .thenReturn(true);
        when(passwordEncoder.encode(changePasswordDTO.getNewPassword()))
                .thenReturn("newHashedPassword");
        // 执行测试
        boolean result = usersService.changePassword(changePasswordDTO);

        // 验证结果
        assertTrue(result);
        // 验证交互
        verify(usersDao, times(1)).selectById(existingUser.getUserId());
        verify(usersDao, times(1)).updateById(any(Users.class));
    }

    @Test
    void testChangePassword_UserNotFound() {
        // 模拟用户不存在
        when(usersDao.selectById(changePasswordDTO.getUserId())).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.changePassword(changePasswordDTO));

        assertEquals("ユーザーが見つかりません", exception.getMessage());

        // 验证交互
        verify(usersDao, times(1)).selectById(changePasswordDTO.getUserId());
        verify(usersDao, never()).updateById(any(Users.class));
    }

    @Test
    void testChangePassword_OldPasswordMismatch() {
        // 模拟用户存在但旧密码不匹配
        when(usersDao.selectById(changePasswordDTO.getUserId())).thenReturn(existingUser);
        when(passwordEncoder.matches(changePasswordDTO.getOldPassword(), existingUser.getPasswordHash()))
                .thenReturn(false);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usersService.changePassword(changePasswordDTO));

        assertEquals("現在のパスワードが正しくありません", exception.getMessage());

        // 验证交互
        verify(usersDao, times(1)).selectById(changePasswordDTO.getUserId());
        verify(usersDao, never()).updateById(any(Users.class));
    }
    @Test
    void testChangePassword_UpdateFails() {
        // 模拟用户存在且旧密码匹配，但更新失败
        when(usersDao.selectById(changePasswordDTO.getUserId())).thenReturn(existingUser);
        when(passwordEncoder.matches(changePasswordDTO.getOldPassword(), existingUser.getPasswordHash()))
                .thenReturn(true);
        when(passwordEncoder.encode(changePasswordDTO.getNewPassword()))
                .thenReturn("newHashedPassword");
        // 模拟更新返回 0（更新失败）
        when(usersDao.updateById(any(Users.class))).thenReturn(0);

        // 执行测试
        boolean result = usersService.changePassword(changePasswordDTO);

        // 验证结果：应该返回 false
        assertFalse(result);

        // 验证交互
        verify(usersDao, times(1)).selectById(existingUser.getUserId());
        verify(usersDao, times(1)).updateById(any(Users.class));
        // 验证密码确实被更新了（即使更新失败，业务逻辑中还是会设置新密码）
        verify(passwordEncoder, times(1)).encode(changePasswordDTO.getNewPassword());
    }
    @Test
    void testGetUsersByRole() {
        when(usersDao.selectList(any())).thenReturn(List.of(existingUser));
        List<Users> users = usersService.getUsersByRole("Admin");
        assertNotNull(users);
        assertEquals(1, users.size());
        verify(usersDao, times(1)).selectList(any());
    }
}
