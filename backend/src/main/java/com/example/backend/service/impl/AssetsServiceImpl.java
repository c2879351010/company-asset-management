package com.example.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.dao.AssetsDao;
import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.dto.assets.AssetsVO;
import com.example.backend.entity.Assets;
import com.example.backend.service.AssetsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 備品マスター(Assets)表服务实现类
 *
 * @author makejava
 * @since 2025-11-04 19:35:20
 */
@Service("assetsService")
public class AssetsServiceImpl extends ServiceImpl<AssetsDao, Assets> implements AssetsService {

    private final AssetsDao assetsDao;

    public AssetsServiceImpl(AssetsDao assetsDao) {
        this.assetsDao = assetsDao;
    }

    @Override
    public boolean createAsset(AssetsDTO assetDTO) {
        Assets asset = new Assets();
        BeanUtils.copyProperties(assetDTO, asset);

        // 验证状态值
        if (!isValidStatus(assetDTO.getStatus())) {
            throw new IllegalArgumentException("状态值无效");
        }

        return this.save(asset);
    }

    @Override
    public boolean updateAsset(String assetId, AssetsDTO assetDTO) {
        Assets existingAsset = this.getById(assetId);
        if (existingAsset == null) {
            throw new RuntimeException("备品不存在");
        }

        // 验证状态值
        if (!isValidStatus(assetDTO.getStatus())) {
            throw new IllegalArgumentException("状态值无效");
        }

        Assets asset = new Assets();
        BeanUtils.copyProperties(assetDTO, asset);
        asset.setAssetId(assetId);

        return this.updateById(asset);
    }

    @Override
    public AssetsVO getAssetById(String assetId) {
        Assets asset = this.getById(assetId);
        if (asset == null) {
            throw new RuntimeException("备品不存在");
        }
        return convertToVO(asset);
    }

    @Override
    public List<AssetsVO> getAllAssets() {
        List<Assets> assets = this.list();
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetsVO> getAssetsByStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("状态值无效");
        }

        List<Assets> assets = assetsDao.selectByStatus(status);
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetsVO> getAssetsByCategory(String category) {
        if (!StringUtils.hasText(category)) {
            throw new IllegalArgumentException("分类不能为空");
        }

        List<Assets> assets = assetsDao.selectByCategory(category);
        return assets.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteAsset(String assetId) {
        return this.removeById(assetId);
    }

    @Override
    public boolean updateAssetStatus(String assetId, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("状态值无效");
        }

        Assets asset = this.getById(assetId);
        if (asset == null) {
            throw new RuntimeException("备品不存在");
        }

        asset.setStatus(status);
        return this.updateById(asset);
    }

    /**
     * 验证状态值是否有效
     */
    private boolean isValidStatus(String status) {
        return "Available".equals(status) ||
                "Borrowed".equals(status) ||
                "Maintenance".equals(status);
    }

    /**
     * 将实体转换为VO
     */
    private AssetsVO convertToVO(Assets asset) {
        AssetsVO vo = new AssetsVO();
        BeanUtils.copyProperties(asset, vo);
        return vo;
    }

}

