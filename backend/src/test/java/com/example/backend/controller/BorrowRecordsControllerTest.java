// java
package com.example.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.dto.borrowrecords.BorrowRecordsDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsQueryDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsVO;
import com.example.backend.entity.BorrowRecords;
import com.example.backend.service.BorrowRecordsService;
import com.example.backend.service.impl.BorrowRecordsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(BorrowRecordsController.class)
//@ContextConfiguration(classes = BorrowRecordsController.class)
@ExtendWith(MockitoExtension.class)
//@AutoConfigureMockMvc
class BorrowRecordsControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BorrowRecordsServiceImpl borrowRecordsService;

    @InjectMocks
    private BorrowRecordsController borrowRecordsController;

    private ObjectMapper objectMapper;
    private BorrowRecordsDTO borrowRecordsDTO;
    private BorrowRecordsVO borrowRecordsVO;
    private BorrowRecordsQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(borrowRecordsController).build();
        objectMapper = new ObjectMapper();

        // Configure ObjectMapper for date handling
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        // Initialize test data - ensure it meets validation rules
        borrowRecordsDTO = new BorrowRecordsDTO();
        borrowRecordsDTO.setUserId("user123");
        borrowRecordsDTO.setAssetId("asset456");

        // Set future dates to satisfy @Future validation
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Tomorrow
        borrowRecordsDTO.setPlannedBorrowDate(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 5); // Day after tomorrow
        borrowRecordsDTO.setPlannedReturnDate(calendar.getTime());

        borrowRecordsDTO.setPurpose("Test borrowing");

        borrowRecordsVO = new BorrowRecordsVO();
        borrowRecordsVO.setRecordId("record123");
        borrowRecordsVO.setUserId("user123");
        borrowRecordsVO.setAssetId("asset456");
        borrowRecordsVO.setStatus("PENDING");

        queryDTO = new BorrowRecordsQueryDTO();
        queryDTO.setUserId("user123");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);
    }

    @Test
    void createBorrowRecord_Success() throws Exception {
        // Setup
        when(borrowRecordsService.createBorrowRecord(any(BorrowRecordsDTO.class)))
                .thenReturn("record123");

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRecordsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("record123"));
    }

    @Test
    void createBorrowRecord_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.createBorrowRecord(any(BorrowRecordsDTO.class)))
                .thenThrow(new RuntimeException("Asset not available"));

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRecordsDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Asset not available"));
    }

    @Test
    void updateBorrowRecord_Success() throws Exception {
        // Setup
        borrowRecordsDTO.setRecordId("record123");
        when(borrowRecordsService.updateBorrowRecord(any(BorrowRecordsDTO.class)))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(put("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRecordsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void updateBorrowRecord_Failure() throws Exception {
        // Setup
        borrowRecordsDTO.setRecordId("record123");
        when(borrowRecordsService.updateBorrowRecord(any(BorrowRecordsDTO.class)))
                .thenThrow(new RuntimeException("Record not found"));

        // Execute & Verify
        mockMvc.perform(put("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRecordsDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void updateBorrowRecord_ServiceReturnFalse() throws Exception {
        // Setup
        borrowRecordsDTO.setRecordId("record123");
        when(borrowRecordsService.updateBorrowRecord(any(BorrowRecordsDTO.class)))
                .thenReturn(false);

        // Execute & Verify
        mockMvc.perform(put("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRecordsDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }

    @Test
    void approveBorrowRecord_Success() throws Exception {
        // Setup
        when(borrowRecordsService.approveBorrowRecord(eq("record123"), anyString()))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/approve")
                        .param("adminNotes", "Approved for borrowing"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void approveBorrowRecord_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.approveBorrowRecord(eq("record123"), anyString()))
                .thenThrow(new RuntimeException("Record not found"));

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/approve")
                        .param("adminNotes", "Approved for borrowing"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void approveBorrowRecord_ServiceReturnFalse() throws Exception {
        // Setup
        when(borrowRecordsService.approveBorrowRecord(eq("record123"), anyString()))
                .thenReturn(false);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/approve")
                        .param("adminNotes", "Approved for borrowing"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));
    }

    @Test
    void rejectBorrowRecord_Success() throws Exception {
        // Setup
        when(borrowRecordsService.rejectBorrowRecord(eq("record123"), anyString()))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/reject")
                        .param("adminNotes", "Insufficient inventory"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void rejectBorrowRecord_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.rejectBorrowRecord(eq("record123"), anyString()))
                .thenThrow(new RuntimeException("Record not found"));

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/reject")
                        .param("adminNotes", "Insufficient inventory"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void rejectBorrowRecord_ServiceReturnFalse() throws Exception {
        // Setup
        when(borrowRecordsService.rejectBorrowRecord(eq("record123"), anyString()))
                .thenReturn(false);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/reject")
                        .param("adminNotes", "Insufficient inventory"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Operation failed"));
    }

    @Test
    void cancelBorrowRecord_Success() throws Exception {
        // Setup
        when(borrowRecordsService.cancelBorrowRecord("record123"))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void cancelBorrowRecord_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.cancelBorrowRecord("record123"))
                .thenThrow(new RuntimeException("Record not found"));

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void cancelBorrowRecord_ServiceReturnFalse() throws Exception {
        // Setup
        when(borrowRecordsService.cancelBorrowRecord("record123"))
                .thenReturn(false);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cancel failed"));
    }

    @Test
    void borrowAsset_Success() throws Exception {
        // Setup
        when(borrowRecordsService.borrowAsset("record123"))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/borrow"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void borrowAsset_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.borrowAsset("record123"))
                .thenThrow(new RuntimeException("Record not found"));

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/borrow"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void borrowAsset_ServiceReturnFalse() throws Exception {
        // Setup
        when(borrowRecordsService.borrowAsset("record123"))
                .thenReturn(false);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/borrow"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Borrow failed"));
    }

    @Test
    void returnAsset_Success() throws Exception {
        // Setup
        when(borrowRecordsService.returnAsset("record123"))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/return"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void returnAsset_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.returnAsset("record123"))
                .thenThrow(new RuntimeException("Record not found"));

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/return"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void returnAsset_ServiceReturnFalse() throws Exception {
        // Setup
        when(borrowRecordsService.returnAsset("record123"))
                .thenReturn(false);

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records/record123/return"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Return failed"));
    }

    @Test
    void getBorrowRecordsPage_Success() throws Exception {
        // Setup
        Page<BorrowRecordsVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(borrowRecordsVO));
        page.setTotal(1);

        when(borrowRecordsService.getBorrowRecordsPage(any(BorrowRecordsQueryDTO.class)))
                .thenReturn(page);

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/page")
                        .param("userId", "user123")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records[0].recordId").value("record123"))
                .andExpect(jsonPath("$.records[0].userId").value("user123"));
    }

    @Test
    void getBorrowRecordsPage_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.getBorrowRecordsPage(any(BorrowRecordsQueryDTO.class)))
                .thenThrow(new RuntimeException("Query exception"));

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/page")
                        .param("userId", "user123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Query failed"));
    }

    @Test
    void getBorrowRecordDetail_Success() throws Exception {
        // Setup
        when(borrowRecordsService.getBorrowRecordDetail("record123"))
                .thenReturn(borrowRecordsVO);

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/record123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value("record123"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    void getBorrowRecordDetail_NotFound() throws Exception {
        // Setup
        when(borrowRecordsService.getBorrowRecordDetail("record999"))
                .thenReturn(null);

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/record999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Record not found"));
    }

    @Test
    void getBorrowRecordDetail_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.getBorrowRecordDetail("record123"))
                .thenThrow(new RuntimeException("Query exception"));

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/record123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Query failed"));
    }

    @Test
    void getUserBorrowRecords_Success() throws Exception {
        // Setup
        List<BorrowRecordsVO> records = Arrays.asList(borrowRecordsVO);
        when(borrowRecordsService.getUserBorrowRecords("user123"))
                .thenReturn(records);

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/user/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordId").value("record123"))
                .andExpect(jsonPath("$[0].userId").value("user123"));
    }

    @Test
    void getUserBorrowRecords_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.getUserBorrowRecords("user123"))
                .thenThrow(new RuntimeException("Query exception"));

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/user/user123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Query failed"));
    }

    @Test
    void checkAssetAvailability_Success() throws Exception {
        // Setup
        when(borrowRecordsService.isAssetAvailable(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(true);

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/check-availability")
                        .param("assetId", "asset456")
                        .param("plannedBorrowDate", "2024-01-15")
                        .param("plannedReturnDate", "2024-01-20"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkAssetAvailability_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.isAssetAvailable(anyString(), any(Date.class), any(Date.class)))
                .thenThrow(new RuntimeException("Check exception"));

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/check-availability")
                        .param("assetId", "asset456")
                        .param("plannedBorrowDate", "2024-01-15")
                        .param("plannedReturnDate", "2024-01-20"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Check failed"));
    }

    @Test
    void checkAssetAvailability_InvalidDateFormat() throws Exception {
        // Execute & Verify - Using invalid date format
        mockMvc.perform(get("/api/borrow-records/check-availability")
                        .param("assetId", "asset456")
                        .param("plannedBorrowDate", "invalid-date")
                        .param("plannedReturnDate", "2024-01-20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOverdueRecords_Success() throws Exception {
        // Setup
        List<BorrowRecordsVO> records = Arrays.asList(borrowRecordsVO);
        when(borrowRecordsService.getOverdueRecords())
                .thenReturn(records);

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordId").value("record123"));
    }

    @Test
    void getOverdueRecords_Failure() throws Exception {
        // Setup
        when(borrowRecordsService.getOverdueRecords())
                .thenThrow(new RuntimeException("Query exception"));

        // Execute & Verify
        mockMvc.perform(get("/api/borrow-records/overdue"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Query failed"));
    }

    @Test
    void createBorrowRecord_ValidationError() throws Exception {
        // Setup - Create an invalid DTO (missing required fields)
        BorrowRecordsDTO invalidDTO = new BorrowRecordsDTO();
        // Not setting userId and assetId, should trigger validation error

        // Execute & Verify
        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest()); // Validation failure returns 400
    }

    @Test
    void createBorrowRecord_DebugValidation() throws Exception {
        // Setup - Create a minimal valid DTO
        BorrowRecordsDTO minimalDTO = new BorrowRecordsDTO();
        minimalDTO.setUserId("user123");
        minimalDTO.setAssetId("asset456");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        minimalDTO.setPlannedBorrowDate(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 5);
        minimalDTO.setPlannedReturnDate(calendar.getTime());

        when(borrowRecordsService.createBorrowRecord(any(BorrowRecordsDTO.class)))
                .thenReturn("record123");

        // Execute and print detailed information
        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalDTO)))
                .andDo(result -> {
                    System.out.println("=== Validation Debug Info ===");
                    System.out.println("Status: " + result.getResponse().getStatus());
                    System.out.println("Content: " + result.getResponse().getContentAsString());
                    System.out.println("Request JSON: " + objectMapper.writeValueAsString(minimalDTO));
                })
                .andExpect(status().isOk());
    }
}
