package com.example.backend.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTOBase {

    @NotBlank(message = "IDは必須です")
    private String userId;
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;


    @NotBlank(message = "名は必須です")
    private String firstName;
    @NotBlank(message = "姓は必須です")
    private String lastName;
    @NotBlank(message = "名のフリカナは必須です")
    private String firstKana;
    @NotBlank(message = "姓のフリカナは必須です")
    private String lastKana;

}
