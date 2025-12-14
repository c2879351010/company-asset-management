package com.example.backend.dto.users;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateVO {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String firstKana;
    private String lastKana;
    private String role;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
