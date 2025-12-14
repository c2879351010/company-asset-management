package com.example.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.borrowrecords.BorrowRecordsDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsQueryDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsVO;
import com.example.backend.entity.BorrowRecords;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 貸出記録(BorrowRecords)表服务接口
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
public interface BorrowRecordsService extends IService<BorrowRecords> {

    @Transactional(rollbackFor = Exception.class)
    String createBorrowRecord(BorrowRecordsDTO dto);


    @Transactional(rollbackFor = Exception.class)
    boolean updateBorrowRecord(BorrowRecordsDTO dto);

    @Transactional(rollbackFor = Exception.class)
    boolean approveBorrowRecord(String recordId, String adminNotes);

    @Transactional(rollbackFor = Exception.class)
    boolean rejectBorrowRecord(String recordId, String adminNotes);

    @Transactional(rollbackFor = Exception.class)
    boolean cancelBorrowRecord(String recordId);

    @Transactional(rollbackFor = Exception.class)
    boolean borrowAsset(String recordId);

    @Transactional(rollbackFor = Exception.class)
    boolean returnAsset(String recordId);

    Page<BorrowRecordsVO> getBorrowRecordsPage(BorrowRecordsQueryDTO queryDTO);

    BorrowRecordsVO getBorrowRecordDetail(String recordId);

    List<BorrowRecordsVO> getUserBorrowRecords(String userId);


    boolean isAssetAvailable(String assetId, Date plannedBorrowDate, Date plannedReturnDate);

    List<BorrowRecordsVO> getOverdueRecords();

    Date getStartOfDay(Date date);

    Date getEndOfDay(Date date);

    BorrowRecordsVO convertToVO(BorrowRecords record);
}

