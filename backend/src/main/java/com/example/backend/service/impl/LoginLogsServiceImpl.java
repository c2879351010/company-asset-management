package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dao.LoginLogsDao;
import com.example.backend.dto.loginlogs.LoginLogQueryDTO;
import com.example.backend.dto.loginlogs.LoginLogVO;
import com.example.backend.dto.loginlogs.UserLogDTO;
import com.example.backend.entity.LoginLogs;
import com.example.backend.service.LoginLogsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * (LoginLogs)表服务实现类
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
@Service("loginLogsService")
public class LoginLogsServiceImpl extends ServiceImpl<LoginLogsDao, LoginLogs> implements LoginLogsService {

    private final LoginLogsDao loginLogsDao;

    public LoginLogsServiceImpl(LoginLogsDao loginLogsDao) {
        this.loginLogsDao = loginLogsDao;
    }

    /**
     * 记录用户登录日志
     */
    @Override
    public void recordLoginLog(UserLogDTO userLogDTO) {
        if (userLogDTO == null) {
            throw new RuntimeException("UserLogDTOが見つかりません");
        }
        LoginLogs loginLog =new LoginLogs();
        BeanUtils.copyProperties(userLogDTO, loginLog);
        loginLogsDao.insert(loginLog);
    }

    /**
     * 根据用户ID查询登录日志
     */
    @Override
    public List<LoginLogVO> getLoginLogsByUserId(String userId) {
        LambdaQueryWrapper<LoginLogs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLogs::getUserId, userId)
                .orderByDesc(LoginLogs::getOperationTime);

        List<LoginLogs> logs = loginLogsDao.selectList(queryWrapper);
        return convertToVOList(logs);
    }
    /**
     * 分页查询登录日志
     */
    @Override
    public Page<LoginLogVO> getLoginLogsPage(LoginLogQueryDTO queryDTO) {
        Page<LoginLogs> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<LoginLogs> queryWrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (StringUtils.isNotBlank(queryDTO.getUserId())) {
            queryWrapper.eq(LoginLogs::getUserId, queryDTO.getUserId());
        }
        if (StringUtils.isNotBlank(queryDTO.getOperationResult())) {
            queryWrapper.eq(LoginLogs::getOperationResult, queryDTO.getOperationResult());
        }
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(LoginLogs::getOperationTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(LoginLogs::getOperationTime, queryDTO.getEndTime());
        }

        queryWrapper.orderByDesc(LoginLogs::getOperationTime);

        Page<LoginLogs> logsPage = loginLogsDao.selectPage(page, queryWrapper);
        return convertToVOPage(logsPage);
    }

    /**
     * 获取用户最近一次成功登录记录
     */
    @Override
    public LoginLogVO getLastLoginLog(String userId, String operationResult) {
        LambdaQueryWrapper<LoginLogs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLogs::getUserId, userId)
                .eq(LoginLogs::getOperationResult, operationResult)
                .orderByDesc(LoginLogs::getOperationTime)
                .apply("ROWNUM = 1");

        LoginLogs lastLog = loginLogsDao.selectOne(queryWrapper);
        return convertToVO(lastLog);
    }

    private LoginLogVO convertToVO(LoginLogs entity) {
        if (entity == null) {
            return null;
        }

        LoginLogVO vo = new LoginLogVO();
        BeanUtils.copyProperties(entity, vo);

        return vo;
    }

    private List<LoginLogVO> convertToVOList(List<LoginLogs> entities) {
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private Page<LoginLogVO> convertToVOPage(Page<LoginLogs> entityPage) {
        Page<LoginLogVO> voPage = new Page<>();
        BeanUtils.copyProperties(entityPage, voPage);
        voPage.setRecords(convertToVOList(entityPage.getRecords()));
        return voPage;
    }

}

