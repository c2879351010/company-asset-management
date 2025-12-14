package com.example.backend.controller;

import com.example.backend.common.enums.OperationType;
import com.example.backend.dto.auth.LoginDTO;
import com.example.backend.dto.auth.LoginVO;
import com.example.backend.dto.auth.RegisterDTO;
import com.example.backend.dto.loginlogs.UserLogDTO;
import com.example.backend.entity.Users;
import com.example.backend.service.LoginLogsService;
import com.example.backend.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final LoginLogsService loginLogService;

    private final UsersService usersService;  // 接口を注入

    public AuthController(LoginLogsService loginLogService, UsersService usersService) {
        this.loginLogService = loginLogService;
        this.usersService = usersService;
    }


    /**
     * ユーザーログイン
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest httpRequest) {
        UserLogDTO userLogDTO = new UserLogDTO();
        userLogDTO.setIpAddress(httpRequest.getRemoteAddr());
        userLogDTO.setUserAgent(httpRequest.getHeader("User-Agent"));
        userLogDTO.setSessionId(httpRequest.getSession().getId());
        userLogDTO.setOperationType(OperationType.LOGIN);
        userLogDTO.setOperationTime(new Date(httpRequest.getSession().getCreationTime()));
        userLogDTO.setLoginEmail(loginDTO.getEmail());
        if (usersService.isEmailExists(loginDTO.getEmail())) {
            userLogDTO.setUserId(usersService.getUserByEmail(loginDTO.getEmail()).getUserId());
        }
        try {
            String token = usersService.authenticateUser(loginDTO.getEmail(), loginDTO.getPassword());
            Users user = usersService.getUserByEmail(loginDTO.getEmail());
            userLogDTO.setOperationResult("SUCCESS");
            loginLogService.recordLoginLog(userLogDTO);
            LoginVO response = new LoginVO(
                    token,
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            userLogDTO.setOperationResult("FAILURE");
            userLogDTO.setDetails(e.getMessage());
            loginLogService.recordLoginLog(userLogDTO);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ユーザー登録
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            Users user = new Users();
            user.setEmail(registerDTO.getEmail());
            user.setFirstName(registerDTO.getFirstName());
            user.setLastName(registerDTO.getLastName());
            user.setRole(registerDTO.getRole());  // デフォルト役割
            user.setFirstKana(registerDTO.getFirstKana());
            user.setLastKana(registerDTO.getLastKana());
            user.setStatus(registerDTO.getStatus());  // デフォルトステータス
            System.out.println(registerDTO);
            boolean success = usersService.registerUser(user, registerDTO.getPassword());

            if (success) {
                return ResponseEntity.ok("ユーザー登録が成功しました");
            } else {
                return ResponseEntity.badRequest().body("ユーザー登録に失敗しました");
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
