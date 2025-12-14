package com.example.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    @NotBlank(message = "ユーザーIDは必須です")
    private String userId;
    @NotBlank(message = "現在のパスワードは必須です")
    private String oldPassword;
    @NotBlank(message = "新しいパスワードは必須です")
    private String newPassword;

}
