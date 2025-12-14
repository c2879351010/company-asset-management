package com.example.backend.common.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTypeTest {

    @Test
    void testGetCode() {
        assertThat(OperationType.LOGIN.getCode()).isEqualTo(1);
        assertThat(OperationType.LOGOUT.getCode()).isEqualTo(2);
        assertThat(OperationType.LOGIN_FAILED.getCode()).isEqualTo(3);
        assertThat(OperationType.FORCE_LOGOUT.getCode()).isEqualTo(4);
        assertThat(OperationType.REFRESH_TOKEN.getCode()).isEqualTo(5);
        assertThat(OperationType.SESSION_EXPIRED.getCode()).isEqualTo(6);
    }

    @Test
    void testGetDescription() {
        assertThat(OperationType.LOGIN.getDescription()).isEqualTo("ローグイン");
        assertThat(OperationType.LOGOUT.getDescription()).isEqualTo("ログアウト");
        assertThat(OperationType.LOGIN_FAILED.getDescription()).isEqualTo("ログイン失敗");
        assertThat(OperationType.FORCE_LOGOUT.getDescription()).isEqualTo("強制ログアウト");
        assertThat(OperationType.REFRESH_TOKEN.getDescription()).isEqualTo("トークン更新");
        assertThat(OperationType.SESSION_EXPIRED.getDescription()).isEqualTo("セッション期限切れ");
    }
}
