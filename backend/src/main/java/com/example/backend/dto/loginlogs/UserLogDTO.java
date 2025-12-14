package com.example.backend.dto.loginlogs;

import com.example.backend.common.enums.OperationType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLogDTO {

    //ユーザーID
    private String userId;

    //IPアドレス
    private String ipAddress;
    //ユーザーエージェント
    private String userAgent;
    //セッションID
    private String sessionId;
    //オペレーション種類
    private OperationType operationType;
    //オペレーション時刻
    private Date operationTime;
    //メールアドレス
    @NotBlank
    private String loginEmail;

    private String operationResult;
    //メッセージ
    private String details;

}
