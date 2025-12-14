package com.example.backend.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OperationType {
    LOGIN(1, "ローグイン"),

    LOGOUT(2, "ログアウト"),

    LOGIN_FAILED(3, "ログイン失敗"),

    FORCE_LOGOUT(4, "強制ログアウト"),

    REFRESH_TOKEN(5, "トークン更新"),

    SESSION_EXPIRED(6, "セッション期限切れ");

    @EnumValue  // 标记序列化字段
    private Integer code;
    private String description;

    OperationType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}