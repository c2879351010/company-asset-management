package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.config.JwtTokenProvider;
import com.example.backend.dto.users.AdminUserUpdateDTO;
import com.example.backend.dto.users.UserUpdateDTOBase;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.dao.UsersDao;
import com.example.backend.dto.auth.ChangePasswordDTO;
import com.example.backend.entity.Users;
import com.example.backend.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ユーザーマスター(Users)表服务实现类
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
@Service("usersService")
public class UsersServiceImpl extends ServiceImpl<UsersDao, Users> implements UsersService {

    @Autowired
    private UsersDao usersDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * メールアドレスでユーザーを検索 - QueryWrapperを使用
     */
    public Users getUserByEmail(String email) {

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);  // WHERE email = #{email}
        return usersDao.selectOne(queryWrapper);
    }

    /**
     * ユーザー認証 - 改良版
     */
    public String authenticateUser(String email, String password) {
        Users user = getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("パスワードが正しくありません");
        }

        return jwtTokenProvider.generateToken(user);
    }

    /**
     * メールアドレスの存在チェック
     */
    @Override
    public boolean isEmailExists(String email) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return usersDao.selectCount(queryWrapper) > 0;
    }

    /**
     * IDでユーザーを検索
     */
    @Override
    public Users getUserById(String id) {
/*        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return usersDao.selectOne(queryWrapper);*/
        return usersDao.selectById(id);
    }

    /**
     * 役割によるユーザー検索
     */
    @Override
    public List<Users> getUsersByRole(String role) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        return usersDao.selectList(queryWrapper);
    }

    /**
     * ユーザー登録
     **/
    public boolean registerUser(Users user, String password) {
        if (user == null || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("ユーザー情報またはパスワードが無効です");
        }
        if (isEmailExists(user.getEmail())) {
            throw new RuntimeException("メールアドレスは既に使用されています");
        }
        String hashedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(hashedPassword);
        user.setUserId(java.util.UUID.randomUUID().toString());
        user.setCreatedAt(java.util.Calendar.getInstance().getTime());
        user.setUpdatedAt(java.util.Calendar.getInstance().getTime());
        int rowsInserted = usersDao.insert(user);
        return rowsInserted > 0;
    }

    @Override
    public UserUpdateVO updateUser(UserUpdateDTOBase request) {
        Users existingUser = usersDao.selectById(request.getUserId());

        if (existingUser == null) {
            throw new RuntimeException("ユーザーが見つかりません");
        }

        Users userToUpdate = new Users();
        BeanUtils.copyProperties(request, userToUpdate);
        userToUpdate.setUpdatedAt(java.util.Calendar.getInstance().getTime());
        userToUpdate.setPasswordHash(existingUser.getPasswordHash());
        if (request instanceof AdminUserUpdateDTO adminRequest) {
            userToUpdate.setRole(adminRequest.getRole());
            userToUpdate.setStatus(adminRequest.getStatus());
        }
        else {
            userToUpdate.setRole(existingUser.getRole());
            userToUpdate.setStatus(existingUser.getStatus());
        }
        int updateNum = usersDao.updateById(userToUpdate);
        if (updateNum == 0) {
            throw new RuntimeException("ユーザーの更新に失敗しました");
        }

        UserUpdateVO userUpdateVO = new UserUpdateVO();
        BeanUtils.copyProperties(userToUpdate, userUpdateVO);
        return userUpdateVO;
    }

    @Override
    public boolean changePassword(ChangePasswordDTO request) {
        Users existingUser = usersDao.selectById(request.getUserId());

        if (existingUser == null) {
            throw new RuntimeException("ユーザーが見つかりません");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPasswordHash())) {
            throw new RuntimeException("現在のパスワードが正しくありません");
        }

        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        existingUser.setPasswordHash(hashedNewPassword);
        existingUser.setUpdatedAt(java.util.Calendar.getInstance().getTime());

        int updateNum = usersDao.updateById(existingUser);
        return updateNum > 0;
    }
}

