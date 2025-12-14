package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    private String password;

    @NotBlank(message = "名は必須です")
    private String firstName;
    @NotBlank(message = "姓は必須です")
    private String lastName;
    @NotBlank(message = "名のフリカナは必須です")
    private String firstKana;
    @NotBlank(message = "姓のフリカナは必須です")
    private String lastKana;
    @NotBlank(message = "役割は必須です")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "AdminとUserの中の一つを選択してください")
    private String role;
    @NotBlank(message = "ステータスは必須です")
    @Pattern(regexp = "^(Active|Lock)$", message = "ActiveとLockの中の一つを選択してください")
    private String status;


}
