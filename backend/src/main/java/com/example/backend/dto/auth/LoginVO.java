package com.example.backend.dto.auth;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private String token;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

}