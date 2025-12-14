package com.example.backend.controller;

import com.example.backend.dto.auth.ChangePasswordDTO;
import com.example.backend.dto.users.UserProfileUpdateDTO;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
public class UserProfileController {
    /**
     * 服务对象
     */
    private final UsersService usersService;

    public UserProfileController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserUpdateVO> UpdateUserByUser(@PathVariable String userId, @Valid @RequestBody UserProfileUpdateDTO request) {
        if (userId == null || request.getUserId() == null || !userId.equals(request.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        UserUpdateVO userUpdateVO = usersService.updateUser(request);
        return ResponseEntity.ok(userUpdateVO);

    }
    @PutMapping("/change-password/{userId}" )
    public ResponseEntity<String> ChangePasswordByUser(@PathVariable String userId, @Valid @RequestBody ChangePasswordDTO request) {
        if (userId == null || request.getUserId() == null || !userId.equals(request.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            usersService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
