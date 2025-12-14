package com.example.backend.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * ユーザーマスター(Users)表实体类
 *
 * @author makejava
 * @since 2025-11-10 19:15:39
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class Users extends Model<Users> {
//ユーザー一意識別子
    @TableId
    private String userId;
//ユーザー氏名
    private String firstName;
//メールアドレス
    private String email;
//ハッシュ化されたパスワード
    private String passwordHash;
//権限
    private String role;

    private String lastName;

    private String firstKana;

    private Date createdAt;

    private Date updatedAt;

    private String lastKana;

    private String status;

}

