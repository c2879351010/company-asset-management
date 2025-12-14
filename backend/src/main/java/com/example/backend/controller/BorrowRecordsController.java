package com.example.backend.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.dto.borrowrecords.BorrowRecordsDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsQueryDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsVO;
import com.example.backend.service.BorrowRecordsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 貸出記録(BorrowRecords)表控制层
 *
 * @author makejava
 * @since 2025-11-04 19:35:20
 */
@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordsController  {
    @Autowired
    private BorrowRecordsService borrowRecordsService;

    @PostMapping
    public ResponseEntity<String> createBorrowRecord(@Valid @RequestBody BorrowRecordsDTO dto) {
        try {
            String recordId = borrowRecordsService.createBorrowRecord(dto);
            return ResponseEntity.ok(recordId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateBorrowRecord(@Valid @RequestBody BorrowRecordsDTO dto) {
        try {
            boolean result = borrowRecordsService.updateBorrowRecord(dto);
            return result ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recordId}/approve")
    public ResponseEntity<?> approveBorrowRecord(@PathVariable String recordId,
                                                 @RequestParam(required = false) String adminNotes) {
        try {
            boolean result = borrowRecordsService.approveBorrowRecord(recordId, adminNotes);
            return result ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recordId}/reject")
    public ResponseEntity<?> rejectBorrowRecord(@PathVariable String recordId,
                                                @RequestParam(required = false) String adminNotes) {
        try {
            boolean result = borrowRecordsService.rejectBorrowRecord(recordId, adminNotes);
            return result ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body("Operation failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recordId}/cancel")
    public ResponseEntity<?> cancelBorrowRecord(@PathVariable String recordId) {
        try {
            boolean result = borrowRecordsService.cancelBorrowRecord(recordId);
            return result ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body("Cancel failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recordId}/borrow")
    public ResponseEntity<?> borrowAsset(@PathVariable String recordId) {
        try {
            boolean result = borrowRecordsService.borrowAsset(recordId);
            return result ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body("Borrow failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recordId}/return")
    public ResponseEntity<?> returnAsset(@PathVariable String recordId) {
        try {
            boolean result = borrowRecordsService.returnAsset(recordId);
            return result ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body("Return failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/page")
    public ResponseEntity<?> getBorrowRecordsPage(@Valid BorrowRecordsQueryDTO queryDTO) {
        try {
            Page<BorrowRecordsVO> page = borrowRecordsService.getBorrowRecordsPage(queryDTO);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Query failed");
        }
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<?> getBorrowRecordDetail(@PathVariable String recordId) {
        try {
            BorrowRecordsVO detail = borrowRecordsService.getBorrowRecordDetail(recordId);
            return detail != null ? ResponseEntity.ok(detail) : ResponseEntity.badRequest().body("Record not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Query failed");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBorrowRecords(@PathVariable String userId) {
        try {
            List<BorrowRecordsVO> records = borrowRecordsService.getUserBorrowRecords(userId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Query failed");
        }
    }

    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAssetAvailability(@RequestParam String assetId,
                                                    @RequestParam String plannedBorrowDate,
                                                    @RequestParam String plannedReturnDate) {
        try {
            // Date format conversion
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date borrowDate = sdf.parse(plannedBorrowDate);
            Date returnDate = sdf.parse(plannedReturnDate);

            boolean available = borrowRecordsService.isAssetAvailable(assetId, borrowDate, returnDate);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Check failed");
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueRecords() {
        try {
            List<BorrowRecordsVO> records = borrowRecordsService.getOverdueRecords();
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Query failed");
        }
    }
}

