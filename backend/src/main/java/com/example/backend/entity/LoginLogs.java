package com.example.backend.entity;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.backend.common.enums.OperationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
/**
 * (LoginLogs)表实体类
 *
 * @author makejava
 * @since 2025-11-13 19:54:17
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginLogs extends Model<LoginLogs> {
    @TableId
//ローグID
    private String logId;
//ユーザーID
    private String userId;
//オペレーション種類
    private OperationType operationType;
//IPアドレス
    private String ipAddress;
//ユーザーエージェント
    private String userAgent;
//メッセージ
    private String details;
//セッションID
    private String sessionId;
//メールアドレス
    private String loginEmail;
//オペレーション時刻
    private Date operationTime;
//オペレーション結果
    private String operationResult;

}

