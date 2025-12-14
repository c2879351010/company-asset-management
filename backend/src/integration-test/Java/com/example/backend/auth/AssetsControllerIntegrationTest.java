package com.example.backend.auth;

import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.service.AssetsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@MapperScan("com.example.backend.dao")
public class AssetsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AssetsDTO testAssetDTO;

    @BeforeEach
    void setUp() {
        testAssetDTO = new AssetsDTO();
        testAssetDTO.setName("Test Asset");
        testAssetDTO.setDescription("Test Description");
        testAssetDTO.setStatus("Available");
        testAssetDTO.setPurchaseDate(new Date());
        testAssetDTO.setImageUrl("http://example.com/image.jpg");
        testAssetDTO.setCategories("Electronics");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void createAsset_ValidInput_ReturnsTrue() throws Exception {
        mockMvc.perform(post("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAssetDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void createAsset_InvalidInput_ReturnsBadRequest() throws Exception {
        testAssetDTO.setName(""); // 触发 @NotBlank 验证

        mockMvc.perform(post("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAssetDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void updateAsset_ValidInput_ReturnsTrue() throws Exception {
        // 先创建一条记录
        assetsService.createAsset(testAssetDTO);

        String assetId = assetsService.getAssetsByStatus(testAssetDTO.getStatus()).getFirst().getAssetId(); // 根据你的 ID 生成策略调整

        testAssetDTO.setName("Updated Asset Name");

        mockMvc.perform(put("/assets/{assetId}", assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAssetDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getAssetById_ExistingId_ReturnsAsset() throws Exception {
        // 先创建一条记录
        assetsService.createAsset(testAssetDTO);
        String assetId = assetsService.getAssetsByStatus(testAssetDTO.getStatus()).getFirst().getAssetId();

        mockMvc.perform(get("/assets/{assetId}", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Asset"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getAllAssets_ReturnsList() throws Exception {
        assetsService.createAsset(testAssetDTO);

        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Asset"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getAssetsByStatus_ValidStatus_ReturnsList() throws Exception {
        assetsService.createAsset(testAssetDTO);

        mockMvc.perform(get("/assets/status/Available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("Available"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void getAssetsByCategory_ValidCategory_ReturnsList() throws Exception {
        assetsService.createAsset(testAssetDTO);

        mockMvc.perform(get("/assets/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].categories").value("Electronics"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void deleteAsset_ExistingId_ReturnsTrue() throws Exception {
        assetsService.createAsset(testAssetDTO);
        String assetId = assetsService.getAssetsByStatus(testAssetDTO.getStatus()).getFirst().getAssetId();

        mockMvc.perform(delete("/assets/{assetId}", assetId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void updateAssetStatus_ValidInput_ReturnsTrue() throws Exception {
        assetsService.createAsset(testAssetDTO);

        String assetId = assetsService.getAssetsByStatus(testAssetDTO.getStatus()).getFirst().getAssetId();

        mockMvc.perform(patch("/assets/{assetId}/status", assetId)
                        .param("status", "Borrowed"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
