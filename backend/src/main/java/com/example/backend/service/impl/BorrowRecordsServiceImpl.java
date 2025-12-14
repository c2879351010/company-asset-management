package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dao.BorrowRecordsDao;
import com.example.backend.dto.borrowrecords.BorrowRecordsDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsQueryDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsVO;
import com.example.backend.entity.BorrowRecords;
import com.example.backend.service.BorrowRecordsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 貸出記録(BorrowRecords)表服务实现类
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
@Service("borrowRecordsService")
public class BorrowRecordsServiceImpl extends ServiceImpl<BorrowRecordsDao, BorrowRecords> implements BorrowRecordsService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBorrowRecord(BorrowRecordsDTO dto) {
        // 检查资产是否可用
        if (!isAssetAvailable(dto.getAssetId(), dto.getPlannedBorrowDate(), dto.getPlannedReturnDate())) {
            throw new RuntimeException("该资产在指定时间段内不可用");
        }

        BorrowRecords record = new BorrowRecords();
        BeanUtils.copyProperties(dto, record);
        record.setStatus("Pending");
        record.setApplyDate(java.util.Calendar.getInstance().getTime());

        boolean saved = save(record);
        if (saved) {
            //log.info("创建借阅记录成功，记录ID: {}", record.getRecordId());
            return record.getRecordId();
        } else {
            throw new RuntimeException("创建借阅记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBorrowRecord(BorrowRecordsDTO dto) {
        BorrowRecords record = getById(dto.getRecordId());
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        // 只有待审批状态的记录可以修改
        if (!"Pending".equals(record.getStatus())) {
            throw new RuntimeException("只有待审批状态的记录可以修改");
        }

        BeanUtils.copyProperties(dto, record);
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveBorrowRecord(String recordId, String adminNotes) {
        BorrowRecords record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        if (!"Pending".equals(record.getStatus())) {
            throw new RuntimeException("只有待审批状态的记录可以审批");
        }

        record.setStatus("Approved");
        record.setAdminNotes(adminNotes);
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectBorrowRecord(String recordId, String adminNotes) {
        BorrowRecords record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        if (!"Pending".equals(record.getStatus())) {
            throw new RuntimeException("只有待审批状态的记录可以拒绝");
        }

        record.setStatus("Rejected");
        record.setAdminNotes(adminNotes);
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelBorrowRecord(String recordId) {
        BorrowRecords record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        // 只有待审批和已批准状态的记录可以取消
        if (!"Pending".equals(record.getStatus()) && !"Approved".equals(record.getStatus())) {
            throw new RuntimeException("当前状态的记录不能取消");
        }

        record.setStatus("Cancelled");
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean borrowAsset(String recordId) {
        BorrowRecords record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        if (!"Approved".equals(record.getStatus())) {
            throw new RuntimeException("只有已批准状态的记录可以借出");
        }

        record.setStatus("Borrowed");
        record.setActualBorrowDate(java.util.Calendar.getInstance().getTime());
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnAsset(String recordId) {
        BorrowRecords record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        if (!"Borrowed".equals(record.getStatus()) && !"Overdue".equals(record.getStatus())) {
            throw new RuntimeException("只有借出或逾期状态的记录可以归还");
        }

        record.setStatus("Returned");
        record.setActualReturnDate(java.util.Calendar.getInstance().getTime());
        return updateById(record);
    }

    @Override
    public Page<BorrowRecordsVO> getBorrowRecordsPage(BorrowRecordsQueryDTO queryDTO) {
        Page<BorrowRecords> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<BorrowRecords> wrapper = buildQueryWrapper(queryDTO);

        Page<BorrowRecords> recordsPage = page(page, wrapper);

        // 转换为VO
        Page<BorrowRecordsVO> voPage = new Page<>();
        BeanUtils.copyProperties(recordsPage, voPage);

        List<BorrowRecordsVO> voList = recordsPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public BorrowRecordsVO getBorrowRecordDetail(String recordId) {
        BorrowRecords record = getById(recordId);
        if (record == null) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    public List<BorrowRecordsVO> getUserBorrowRecords(String userId) {
        LambdaQueryWrapper<BorrowRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecords::getUserId, userId)
                .orderByDesc(BorrowRecords::getCreatedAt);

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAssetAvailable(String assetId, Date plannedBorrowDate, Date plannedReturnDate) {
        LambdaQueryWrapper<BorrowRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecords::getAssetId, assetId)
                .in(BorrowRecords::getStatus, "Approved", "Borrowed", "Overdue")
                .and(w -> w.and(q -> q.le(BorrowRecords::getPlannedBorrowDate, plannedReturnDate)
                                .ge(BorrowRecords::getPlannedReturnDate, plannedBorrowDate))
                        .or(q -> q.le(BorrowRecords::getPlannedBorrowDate, plannedReturnDate)
                                .ge(BorrowRecords::getPlannedReturnDate, plannedBorrowDate)));

        return count(wrapper) == 0;
    }

    @Override
    public List<BorrowRecordsVO> getOverdueRecords() {
        LambdaQueryWrapper<BorrowRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(BorrowRecords::getStatus, "Borrowed")
                .lt(BorrowRecords::getPlannedReturnDate, java.util.Calendar.getInstance().getTime());

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<BorrowRecords> buildQueryWrapper(BorrowRecordsQueryDTO queryDTO) {
        LambdaQueryWrapper<BorrowRecords> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getUserId() != null) {
            wrapper.eq(BorrowRecords::getUserId, queryDTO.getUserId());
        }
        if (queryDTO.getAssetId() != null) {
            wrapper.eq(BorrowRecords::getAssetId, queryDTO.getAssetId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(BorrowRecords::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getApplyDateStart() != null) {
            wrapper.ge(BorrowRecords::getApplyDate, getStartOfDay(queryDTO.getApplyDateStart()));
        }
        if (queryDTO.getApplyDateEnd() != null) {
            wrapper.le(BorrowRecords::getApplyDate, getEndOfDay(queryDTO.getApplyDateEnd()));
        }
        if (queryDTO.getPlannedBorrowDateStart() != null) {
            wrapper.ge(BorrowRecords::getPlannedBorrowDate, getStartOfDay(queryDTO.getPlannedBorrowDateStart()));
        }
        if (queryDTO.getPlannedBorrowDateEnd() != null) {
            wrapper.le(BorrowRecords::getPlannedBorrowDate, getEndOfDay(queryDTO.getPlannedBorrowDateEnd()));
        }

        wrapper.orderByDesc(BorrowRecords::getCreatedAt);
        return wrapper;
    }

    @Override
    public Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        System.out.println(date);
        System.out.println(calendar);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println(calendar.getTime());
        return calendar.getTime();
    }

    @Override
    public Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    @Override
    public BorrowRecordsVO convertToVO(BorrowRecords record) {
        BorrowRecordsVO vo = new BorrowRecordsVO();
        BeanUtils.copyProperties(record, vo);

        // 设置状态描述
        vo.setStatusDesc(record.getStatus());

        // 这里可以添加查询用户信息和资产信息的逻辑
        // vo.setUserName(userService.getUserNameById(record.getUserId()));
        // vo.setAssetName(assetService.getAssetNameById(record.getAssetId()));

        return vo;
    }

}

