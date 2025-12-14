package com.example.backend.auth;

import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsDTO;
import com.example.backend.entity.Users;
import com.example.backend.service.AssetsService;
import com.example.backend.service.BorrowRecordsService;
import com.example.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@MapperScan("com.example.backend.dao")
public class BorrowRecordsControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BorrowRecordsService borrowRecordsService;

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private BorrowRecordsDTO testBorrowRecordDTO;
    private String testAssetId;
    private String testUserId = UUID.randomUUID().toString();
    private AssetsDTO testAssetDTO;
    private final String rawPassword = "Test@1234";
    @BeforeEach
    void setUp() throws Exception {
        // 先创建一个测试资产
        testAssetDTO = new AssetsDTO();
        testAssetDTO.setName("Test Asset");
        testAssetDTO.setDescription("Test Description");
        testAssetDTO.setStatus("Available");
        testAssetDTO.setPurchaseDate(new Date());
        testAssetDTO.setImageUrl("http://example.com/image.jpg");
        testAssetDTO.setCategories("Electronics");
        assetsService.createAsset(testAssetDTO);
        testAssetId = assetsService.getAllAssets().getFirst().getAssetId();

        Users user = new Users();
        user.setUserId(testUserId);
        user.setEmail("test@example.com");
        user.setFirstName("太郎");
        user.setLastName("山田");
        user.setFirstKana("タロウ");
        user.setLastKana("ヤマダ");
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole("ADMIN");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus("Active");
        usersService.registerUser(user, rawPassword);
        usersService.getUserByEmail("test@example.com");
        testUserId = user.getUserId();
        // 设置测试借阅记录DTO
        testBorrowRecordDTO = new BorrowRecordsDTO();
        testBorrowRecordDTO.setUserId(testUserId);
        testBorrowRecordDTO.setAssetId(testAssetId);
        testBorrowRecordDTO.setPurpose("Testing purposes");
        // 设置未来日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        testBorrowRecordDTO.setPlannedBorrowDate(calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 7);
        testBorrowRecordDTO.setPlannedReturnDate(calendar.getTime());


    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void createBorrowRecord_ValidInput_ReturnsRecordId() throws Exception {
        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBorrowRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void createBorrowRecord_InvalidInput_ReturnsBadRequest() throws Exception {
        testBorrowRecordDTO.setUserId(""); // 触发 @NotBlank 验证

        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBorrowRecordDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void updateBorrowRecord_ValidInput_ReturnsTrue() throws Exception {
        // 先创建一条借阅记录
        System.out.println(testBorrowRecordDTO.getStatus());
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        testBorrowRecordDTO.setRecordId(recordId);
        testBorrowRecordDTO.setPurpose("Updated purpose");

        mockMvc.perform(put("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBorrowRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void approveBorrowRecord_ValidInput_ReturnsTrue() throws Exception {
        // 先创建一条待审批的借阅记录
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        mockMvc.perform(post("/api/borrow-records/{recordId}/approve", recordId)
                        .param("adminNotes", "Approved for testing"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void rejectBorrowRecord_ValidInput_ReturnsTrue() throws Exception {
        // 先创建一条待审批的借阅记录
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        mockMvc.perform(post("/api/borrow-records/{recordId}/reject", recordId)
                        .param("adminNotes", "Rejected for testing"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void cancelBorrowRecord_ValidInput_ReturnsTrue() throws Exception {
        // 先创建一条借阅记录
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        mockMvc.perform(post("/api/borrow-records/{recordId}/cancel", recordId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void borrowAsset_ValidInput_ReturnsTrue() throws Exception {
        // 先创建并审批一条借阅记录
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);
        borrowRecordsService.approveBorrowRecord(recordId, "Approved");

        mockMvc.perform(post("/api/borrow-records/{recordId}/borrow", recordId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void returnAsset_ValidInput_ReturnsTrue() throws Exception {
        // 先创建、审批并借出资产
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);
        borrowRecordsService.approveBorrowRecord(recordId, "Approved");
        borrowRecordsService.borrowAsset(recordId);

        mockMvc.perform(post("/api/borrow-records/{recordId}/return", recordId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void getBorrowRecordsPage_ValidInput_ReturnsPage() throws Exception {
        // 先创建一条借阅记录
        borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        mockMvc.perform(get("/api/borrow-records/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void getBorrowRecordDetail_ExistingId_ReturnsDetail() throws Exception {
        // 先创建一条借阅记录
        String recordId = borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        mockMvc.perform(get("/api/borrow-records/{recordId}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value(recordId));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void getUserBorrowRecords_ValidUserId_ReturnsList() throws Exception {
        // 先创建一条借阅记录
        borrowRecordsService.createBorrowRecord(testBorrowRecordDTO);

        mockMvc.perform(get("/api/borrow-records/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void checkAssetAvailability_ValidInput_ReturnsBoolean() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 10);
        String borrowDate = String.format("%tF", calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, 5);
        String returnDate = String.format("%tF", calendar.getTime());

        mockMvc.perform(get("/api/borrow-records/check-availability")
                        .param("assetId", testAssetId)
                        .param("plannedBorrowDate", borrowDate)
                        .param("plannedReturnDate", returnDate))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.either(org.hamcrest.Matchers.is("true"))
                        .or(org.hamcrest.Matchers.is("false"))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getOverdueRecords_ReturnsList() throws Exception {
        mockMvc.perform(get("/api/borrow-records/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"User"})
    void createBorrowRecord_PastDate_ReturnsBadRequest() throws Exception {
        // 设置过去的日期，应该触发验证失败
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        testBorrowRecordDTO.setPlannedBorrowDate(calendar.getTime());
        testBorrowRecordDTO.setPlannedReturnDate(calendar.getTime());

        mockMvc.perform(post("/api/borrow-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBorrowRecordDTO)))
                .andExpect(status().isBadRequest());
    }
}
