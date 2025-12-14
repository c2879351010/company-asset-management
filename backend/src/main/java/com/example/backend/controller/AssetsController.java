package com.example.backend.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.dto.assets.AssetsVO;
import com.example.backend.entity.Assets;
import com.example.backend.service.AssetsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 備品マスター(Assets)表控制层
 *
 * @author makejava
 * @since 2025-11-04 19:35:16
 */
@RestController
@RequestMapping("assets")
public class AssetsController {
    private final AssetsService assetsService;

    public AssetsController(AssetsService assetsService) {
        this.assetsService = assetsService;
    }

    /**
     * 创建备品
     */
    @PostMapping
    public ResponseEntity<Boolean> createAsset(@Valid @RequestBody AssetsDTO assetDTO) {
        boolean result = assetsService.createAsset(assetDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新备品
     */
    @PutMapping("/{assetId}")
    public ResponseEntity<Boolean> updateAsset(
            @PathVariable String assetId,
            @Valid @RequestBody AssetsDTO assetDTO) {
        boolean result = assetsService.updateAsset(assetId, assetDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID获取备品详情
     */
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetsVO> getAssetById(@PathVariable String assetId) {
        AssetsVO assetVO = assetsService.getAssetById(assetId);
        return ResponseEntity.ok(assetVO);
    }

    /**
     * 获取所有备品列表
     */
    @GetMapping
    public ResponseEntity<List<AssetsVO>> getAllAssets() {
        List<AssetsVO> assets = assetsService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    /**
     * 根据状态查询备品
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AssetsVO>> getAssetsByStatus(@PathVariable String status) {
        List<AssetsVO> assets = assetsService.getAssetsByStatus(status);
        return ResponseEntity.ok(assets);
    }

    /**
     * 根据分类查询备品
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<AssetsVO>> getAssetsByCategory(@PathVariable String category) {
        List<AssetsVO> assets = assetsService.getAssetsByCategory(category);
        return ResponseEntity.ok(assets);
    }

    /**
     * 删除备品
     */
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Boolean> deleteAsset(@PathVariable String assetId) {
        boolean result = assetsService.deleteAsset(assetId);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新备品状态
     */
    @PatchMapping("/{assetId}/status")
    public ResponseEntity<Boolean> updateAssetStatus(
            @PathVariable String assetId,
            @RequestParam String status) {
        boolean result = assetsService.updateAssetStatus(assetId, status);
        return ResponseEntity.ok(result);
    }
}

