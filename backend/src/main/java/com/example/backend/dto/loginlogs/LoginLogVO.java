package com.example.backend.dto.loginlogs;


import com.example.backend.common.enums.OperationType;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogVO {
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
    //オペレーション結果
    private String operationResult;
    //メッセージ
    private String details;
    //セッションID
    private String sessionId;
    //メールアドレス
    private String loginEmail;
    //オペレーション時刻
    private Date operationTime;

}
