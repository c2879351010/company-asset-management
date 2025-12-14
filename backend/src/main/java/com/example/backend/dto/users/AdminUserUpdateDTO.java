package com.example.backend.dto.users;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateDTO extends UserUpdateDTOBase {
    @NotBlank(message = "役割は必須です")
    private String role;
    @NotBlank(message = "ステータスは必須です")
    private String status;

}
