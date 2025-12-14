package com.example.backend.controller;




import com.example.backend.dto.users.AdminUserUpdateDTO;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * ユーザーマスター(Users)表控制层
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/users")
public class UsersAdminController {
    /**
     * 服务对象
     */
    @Autowired
    private UsersService usersService;

    @GetMapping("/status")
    public  ResponseEntity<?> GetStatus() {
        return ResponseEntity.ok(usersService.list());
    }

    @GetMapping
    public ResponseEntity<?> GetAllUsers(@RequestParam(defaultValue = "0" )int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String role) {
        return ResponseEntity.ok(usersService.list());
    }

    @PutMapping
    public ResponseEntity<UserUpdateVO> UpdateUserByAdmin(@Valid @RequestBody AdminUserUpdateDTO request) {

        UserUpdateVO userUpdateVO = usersService.updateUser(request);
        return ResponseEntity.ok(userUpdateVO);

    }

}

