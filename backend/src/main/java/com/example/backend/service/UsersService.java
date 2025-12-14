package com.example.backend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.auth.ChangePasswordDTO;
import com.example.backend.dto.users.UserUpdateDTOBase;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.entity.Users;

import java.util.List;


/**
 * ユーザーマスター(Users)表服务接口
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
public interface UsersService extends IService<Users> {

    /**
     * ユーザー認証
     */
    String authenticateUser(String email, String password);

    /**
     * メールアドレスでユーザーを検索
     */
    Users getUserByEmail(String email);

    /**
     * メールアドレスの存在チェック
     */
    boolean isEmailExists(String email);

    /**
     * IDでユーザーを検索
     */
    Users getUserById(String id);

    List<Users> getUsersByRole(String role);

    /**
     * ユーザー登録
     **/
    boolean registerUser(Users user, String password);

    /**
     * 管理者によるユーザー情報更新
     **/
    UserUpdateVO updateUser(UserUpdateDTOBase request);

    boolean changePassword(ChangePasswordDTO request);
}

