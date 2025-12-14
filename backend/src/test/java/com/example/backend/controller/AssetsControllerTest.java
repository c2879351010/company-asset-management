package com.example.backend.controller;

import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.dto.assets.AssetsVO;
import com.example.backend.service.AssetsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AssetsControllerTest {
    @Mock
    private AssetsService mockAssetsService;

    @InjectMocks
    private AssetsController assetsController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(assetsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void createAsset_returnsTrue() throws Exception {
        when(mockAssetsService.createAsset(any(AssetsDTO.class))).thenReturn(true);

        AssetsDTO req = new AssetsDTO();
        req.setName("test");
        req.setStatus("active");
        req.setCategories("category1");

        mockMvc.perform(post("/assets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void createAsset_returnsFalse() throws Exception {
        when(mockAssetsService.createAsset(any(AssetsDTO.class))).thenReturn(false);

        AssetsDTO req = new AssetsDTO();
        req.setName("test");
        req.setStatus("active");
        req.setCategories("category1");

        mockMvc.perform(post("/assets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void updateAsset_returnsTrue_andFalse() throws Exception {
        when(mockAssetsService.updateAsset(eq("a1"), any(AssetsDTO.class))).thenReturn(true);

        AssetsDTO req = new AssetsDTO();
        req.setName("test");
        req.setStatus("active");
        req.setCategories("category1");
        req.setAssetId("a1");

        mockMvc.perform(put("/assets/{assetId}", "a1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        when(mockAssetsService.updateAsset(eq("a1"), any(AssetsDTO.class))).thenReturn(false);

        mockMvc.perform(put("/assets/{assetId}", "a1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getAssetById_found_and_notFound() throws Exception {
        AssetsVO vo = new AssetsVO();
        vo.setAssetId("a1");
        vo.setName("AssetName");
/*        vo.setDescription("AssetDescription");
        vo.setCategories("category1");
        vo.setStatus("active");
        vo.setPurchaseDate(new Date());
        vo.setImageUrl("imageUrl");*/
        when(mockAssetsService.getAssetById("a1")).thenReturn(vo);

        mockMvc.perform(get("/assets/{assetId}", "a1")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("a1")))
                .andExpect(content().contentTypeCompatibleWith("application/json"));

        when(mockAssetsService.getAssetById("missing")).thenReturn(null);

        mockMvc.perform(get("/assets/{assetId}", "missing")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(isEmptyString()));
    }

    @Test
    void getAllAssets_returnsList_andEmpty() throws Exception {
        AssetsVO vo = new AssetsVO();
        vo.setAssetId("a1");
        when(mockAssetsService.getAllAssets()).thenReturn((List) List.of(vo));

        mockMvc.perform(get("/assets")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("a1")))
                .andExpect(content().contentTypeCompatibleWith("application/json"));

        when(mockAssetsService.getAllAssets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/assets")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void getAssetsByStatus_andByCategory() throws Exception {
        AssetsVO vo = new AssetsVO();
        vo.setAssetId("s1");
        when(mockAssetsService.getAssetsByStatus("active")).thenReturn((List) List.of(vo));

        mockMvc.perform(get("/assets/status/{status}", "active")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("s1")));

        AssetsVO cvo = new AssetsVO();
        cvo.setAssetId("c1");
        when(mockAssetsService.getAssetsByCategory("cat")).thenReturn((List) List.of(cvo));

        mockMvc.perform(get("/assets/category/{category}", "cat")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("c1")));
    }

    @Test
    void deleteAsset_returnsTrue_andFalse() throws Exception {
        when(mockAssetsService.deleteAsset("d1")).thenReturn(true);

        mockMvc.perform(delete("/assets/{assetId}", "d1")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        when(mockAssetsService.deleteAsset("d1")).thenReturn(false);

        mockMvc.perform(delete("/assets/{assetId}", "d1")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void updateAssetStatus_returnsTrue_andFalse() throws Exception {
        when(mockAssetsService.updateAssetStatus("p1", "available")).thenReturn(true);

        mockMvc.perform(patch("/assets/{assetId}/status", "p1")
                        .param("status", "available")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        when(mockAssetsService.updateAssetStatus("p1", "available")).thenReturn(false);

        mockMvc.perform(patch("/assets/{assetId}/status", "p1")
                        .param("status", "available")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
