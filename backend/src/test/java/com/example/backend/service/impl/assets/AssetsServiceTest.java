package com.example.backend.service.impl.assets;

import com.example.backend.dao.AssetsDao;
import com.example.backend.dto.assets.AssetsDTO;
import com.example.backend.dto.assets.AssetsVO;
import com.example.backend.entity.Assets;
import com.example.backend.service.impl.AssetsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AssetsServiceTest {

    @Mock
    private AssetsDao assetsDao;

    @Spy
    @InjectMocks
    private AssetsServiceImpl assetsService;

    // helper builders
    private AssetsDTO dtoWithStatus(String status) {
        AssetsDTO dto = new AssetsDTO();
        try {
            // try common setters
            dto.getClass().getMethod("setStatus", String.class).invoke(dto, status);
        } catch (Exception ignored) {
            // fallback: use BeanUtils on a simple Assets object
            Assets tmp = new Assets();
            try {
                tmp.getClass().getMethod("setStatus", String.class).invoke(tmp, status);
            } catch (Exception ignore) {}
            BeanUtils.copyProperties(tmp, dto);
        }
        return dto;
    }

    private Assets assetWithIdAndStatus(String id, String status) {
        Assets a = new Assets();
        try {
            a.getClass().getMethod("setAssetId", String.class).invoke(a, id);
            a.getClass().getMethod("setStatus", String.class).invoke(a, status);
        } catch (Exception ignored) {}
        return a;
    }

    private Assets dtoCopiedToAsset(AssetsDTO dto) {
        Assets a = new Assets();
        BeanUtils.copyProperties(dto, a);
        return a;
    }

    @Test
    void createAsset_success() {
        AssetsDTO dto = dtoWithStatus("Available");
        doReturn(true).when(assetsService).save(any(Assets.class));

        boolean result = assetsService.createAsset(dto);

        assertTrue(result);
        verify(assetsService).save(any(Assets.class));
    }

    @Test
    void createAsset_invalidStatus_throws() {
        AssetsDTO dto = dtoWithStatus("InvalidStatus");

        assertThrows(IllegalArgumentException.class, () -> assetsService.createAsset(dto));
        verify(assetsService, never()).save(any());
    }

    @Test
    void updateAsset_success() {
        String id = "id-1";
        AssetsDTO dto = dtoWithStatus("Borrowed");
        Assets existing = assetWithIdAndStatus(id, "Available");
        doReturn(existing).when(assetsService).getById(id);
        doReturn(true).when(assetsService).updateById(any(Assets.class));

        boolean result = assetsService.updateAsset(id, dto);

        assertTrue(result);
        ArgumentCaptor<Assets> captor = ArgumentCaptor.forClass(Assets.class);
        verify(assetsService).updateById(captor.capture());
        Assets passed = captor.getValue();
        // ensure id assigned
        try {
            Object val = passed.getClass().getMethod("getAssetId").invoke(passed);
            assertEquals(id, val);
        } catch (Exception ignored) {}
    }

    @Test
    void updateAsset_notFound_throws() {
        String id = "missing";
        AssetsDTO dto = dtoWithStatus("Available");
        doReturn(null).when(assetsService).getById(id);

        assertThrows(RuntimeException.class, () -> assetsService.updateAsset(id, dto));
        verify(assetsService, never()).updateById(any());
    }

    @Test
    void updateAsset_invalidStatus_throws() {
        String id = "id-2";
        Assets existing = assetWithIdAndStatus(id, "Available");
        doReturn(existing).when(assetsService).getById(id);
        AssetsDTO dto = dtoWithStatus("Bad");

        assertThrows(IllegalArgumentException.class, () -> assetsService.updateAsset(id, dto));
        verify(assetsService, never()).updateById(any());
    }

    @Test
    void getAssetById_success() {
        String id = "id-3";
        Assets a = assetWithIdAndStatus(id, "Available");
        try {
            a.getClass().getMethod("setAssetId", String.class).invoke(a, id);
        } catch (Exception ignored) {}
        doReturn(a).when(assetsService).getById(id);

        AssetsVO vo = assetsService.getAssetById(id);

        assertNotNull(vo);
        // check id copied
        try {
            Object vid = vo.getClass().getMethod("getAssetId").invoke(vo);
            assertEquals(id, vid);
        } catch (Exception ignored) {}
    }

    @Test
    void getAssetById_notFound_throws() {
        String id = "no";
        doReturn(null).when(assetsService).getById(id);

        assertThrows(RuntimeException.class, () -> assetsService.getAssetById(id));
    }

    @Test
    void getAllAssets_returnsMappedList() {
        Assets a1 = assetWithIdAndStatus("a1", "Available");
        Assets a2 = assetWithIdAndStatus("a2", "Borrowed");
        doReturn(Arrays.asList(a1, a2)).when(assetsService).list();

        List<AssetsVO> list = assetsService.getAllAssets();

        assertEquals(2, list.size());
    }

    @Test
    void getAssetsByStatus_success() {
        String status = "Maintenance";
        Assets a = assetWithIdAndStatus("s1", status);
        when(assetsDao.selectByStatus(status)).thenReturn(Collections.singletonList(a));

        List<AssetsVO> list = assetsService.getAssetsByStatus(status);

        assertEquals(1, list.size());
        verify(assetsDao).selectByStatus(status);
    }

    @Test
    void getAssetsByStatus_invalidStatus_throws() {
        assertThrows(IllegalArgumentException.class, () -> assetsService.getAssetsByStatus("BadStatus"));
        verifyNoInteractions(assetsDao);
    }

    @Test
    void getAssetsByCategory_success() {
        String category = "Laptop";
        Assets a = assetWithIdAndStatus("c1", "Available");
        when(assetsDao.selectByCategory(category)).thenReturn(Collections.singletonList(a));

        List<AssetsVO> list = assetsService.getAssetsByCategory(category);

        assertEquals(1, list.size());
        verify(assetsDao).selectByCategory(category);
    }

    @Test
    void getAssetsByCategory_empty_throws() {
        assertThrows(IllegalArgumentException.class, () -> assetsService.getAssetsByCategory(""));
        verifyNoInteractions(assetsDao);
    }

    @Test
    void deleteAsset_delegatesToRemoveById() {
        String id = "del-1";
        doReturn(true).when(assetsService).removeById(id);

        assertTrue(assetsService.deleteAsset(id));
        verify(assetsService).removeById(id);
    }

    @Test
    void updateAssetStatus_success() {
        String id = "us-1";
        String newStatus = "Available";
        Assets existing = assetWithIdAndStatus(id, "Borrowed");
        doReturn(existing).when(assetsService).getById(id);
        doReturn(true).when(assetsService).updateById(any(Assets.class));

        boolean result = assetsService.updateAssetStatus(id, newStatus);

        assertTrue(result);
        verify(assetsService).updateById(any(Assets.class));
    }

    @Test
    void updateAssetStatus_invalidStatus_throws() {
        assertThrows(IllegalArgumentException.class, () -> assetsService.updateAssetStatus("x", "Bad"));
    }

    @Test
    void updateAssetStatus_assetNotFound_throws() {
        String id = "notfound";
        doReturn(null).when(assetsService).getById(id);
        assertThrows(RuntimeException.class, () -> assetsService.updateAssetStatus(id, "Available"));
    }
}
