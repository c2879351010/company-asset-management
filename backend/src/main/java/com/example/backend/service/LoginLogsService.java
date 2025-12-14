package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.loginlogs.LoginLogQueryDTO;
import com.example.backend.dto.loginlogs.LoginLogVO;
import com.example.backend.dto.loginlogs.UserLogDTO;
import com.example.backend.entity.LoginLogs;

import java.util.List;

/**
 * (LoginLogs)表服务接口
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
public interface LoginLogsService extends IService<LoginLogs> {


    void recordLoginLog(UserLogDTO loginLogsRequest);

   List<LoginLogVO> getLoginLogsByUserId(String userId);

    Page<LoginLogVO> getLoginLogsPage(LoginLogQueryDTO queryDTO);


    LoginLogVO getLastLoginLog(String userId, String operationResult);

}

