package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.dto.assets.AssetsVO;
import com.example.backend.entity.Assets;

import java.util.List;

/**
 * 備品マスター(Assets)表服务接口
 *
 * @author makejava
 * @since 2025-11-04 19:35:19
 */
public interface AssetsService extends IService<Assets> {

    /**
     * 创建备品
     */
    boolean createAsset(AssetsDTO assetDTO);

    /**
     * 更新备品
     */
    boolean updateAsset(String assetId, AssetsDTO assetDTO);

    /**
     * 根据ID获取备品详情
     */
    AssetsVO getAssetById(String assetId);

    /**
     * 获取所有备品列表
     */
    List<AssetsVO> getAllAssets();

    /**
     * 根据状态查询备品
     */
    List<AssetsVO> getAssetsByStatus(String status);

    /**
     * 根据分类查询备品
     */
    List<AssetsVO> getAssetsByCategory(String category);

    /**
     * 删除备品
     */
    boolean deleteAsset(String assetId);

    /**
     * 更新备品状态
     */
    boolean updateAssetStatus(String assetId, String status);
}

