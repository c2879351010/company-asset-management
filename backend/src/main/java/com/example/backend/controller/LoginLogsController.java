package com.example.backend.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.dto.loginlogs.LoginLogQueryDTO;
import com.example.backend.dto.loginlogs.LoginLogVO;
import com.example.backend.service.LoginLogsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * (LoginLogs)表控制层
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
@RestController
@RequestMapping("/api/login-logs")
public class LoginLogsController {

    private final LoginLogsService loginLogService;

    public LoginLogsController(LoginLogsService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoginLogVO>> getLoginLogsByUserId(@PathVariable String userId) {
        try {
            List<LoginLogVO> logs = loginLogService.getLoginLogsByUserId(userId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/last-login/{userId}")
    public ResponseEntity<LoginLogVO> getLastLoginLog(@PathVariable String userId) {
        try {
            LoginLogVO lastLog = loginLogService.getLastLoginLog(userId, "SUCCESS");
            return ResponseEntity.ok(lastLog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Page<LoginLogVO>> getLoginLogsPage( LoginLogQueryDTO queryDTO) {
        try {
            Page<LoginLogVO> page = loginLogService.getLoginLogsPage(queryDTO);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

