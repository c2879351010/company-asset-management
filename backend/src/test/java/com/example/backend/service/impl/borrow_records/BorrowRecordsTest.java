package com.example.backend.service.impl.borrow_records;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.dao.BorrowRecordsDao;
import com.example.backend.dto.borrowrecords.BorrowRecordsDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsQueryDTO;
import com.example.backend.dto.borrowrecords.BorrowRecordsVO;
import com.example.backend.entity.BorrowRecords;
import com.example.backend.service.impl.BorrowRecordsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowRecordsTest {
    @Mock
    private BorrowRecordsDao borrowRecordsDao;

    @Spy
    @InjectMocks
    private BorrowRecordsServiceImpl service;

    private BorrowRecords buildRecord(String id, String status) {
        BorrowRecords r = new BorrowRecords();
        try {
            r.getClass().getMethod("setRecordId", String.class).invoke(r, id);
            r.getClass().getMethod("setStatus", String.class).invoke(r, status);
        } catch (Exception ignored) {}
        r.setApplyDate(new Date());
        r.setCreatedAt(new Date());
        return r;
    }

    private BorrowRecordsDTO buildDto(String recordId, String assetId, Date pb, Date pr) {
        BorrowRecordsDTO dto = new BorrowRecordsDTO();
        try {
            if (recordId != null) dto.getClass().getMethod("setRecordId", String.class).invoke(dto, recordId);
            if (assetId != null) dto.getClass().getMethod("setAssetId", String.class).invoke(dto, assetId);
            dto.getClass().getMethod("setPlannedBorrowDate", Date.class).invoke(dto, pb);
            dto.getClass().getMethod("setPlannedReturnDate", Date.class).invoke(dto, pr);
        } catch (Exception ignored) {}
        return dto;
    }

    @Test
    void createBorrowRecord_success() {
        Date pb = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(pb);
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date pr = c.getTime();

        BorrowRecordsDTO dto = buildDto(null, "asset-1", pb, pr);

        doReturn(true).when(service).isAssetAvailable("asset-1", pb, pr);

        // when saving assign an id to simulate DB-generated id
        doAnswer(inv -> {
            BorrowRecords arg = inv.getArgument(0);
            try { arg.getClass().getMethod("setRecordId", String.class).invoke(arg, "rid-1"); } catch (Exception ignored) {}
            return true;
        }).when(service).save(any(BorrowRecords.class));

        String id = service.createBorrowRecord(dto);
        assertEquals("rid-1", id);
        verify(service).save(any(BorrowRecords.class));
    }

    @Test
    void createBorrowRecord_assetUnavailable_throws() {
        Date pb = new Date();
        Date pr = pb;
        BorrowRecordsDTO dto = buildDto(null, "asset-1", pb, pr);
        doReturn(false).when(service).isAssetAvailable("asset-1", pb, pr);

        assertThrows(RuntimeException.class, () -> service.createBorrowRecord(dto));
        verify(service, never()).save(any());
    }

    @Test
    void createBorrowRecord_SaveFailure_throws() {
        Date pb = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(pb);
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date pr = c.getTime();

        BorrowRecordsDTO dto = buildDto(null, "asset-1", pb, pr);

        doReturn(true).when(service).isAssetAvailable("asset-1", pb, pr);
        doReturn(false).when(service).save(any(BorrowRecords.class));

        assertThrows(RuntimeException.class, () -> service.createBorrowRecord(dto));
        verify(service).save(any(BorrowRecords.class));
    }

    @Test
    void updateBorrowRecord_success() {
        BorrowRecords existing = buildRecord("r1", "PENDING");
        BorrowRecordsDTO dto = new BorrowRecordsDTO();
        try { dto.getClass().getMethod("setRecordId", String.class).invoke(dto, "r1"); } catch (Exception ignored) {}
        doReturn(existing).when(service).getById("r1");
        doReturn(true).when(service).updateById(any(BorrowRecords.class));

        boolean res = service.updateBorrowRecord(dto);
        assertTrue(res);
        verify(service).updateById(any(BorrowRecords.class));
    }

    @Test
    void updateBorrowRecord_notFound_throws() {
        BorrowRecordsDTO dto = new BorrowRecordsDTO();
        try { dto.getClass().getMethod("setRecordId", String.class).invoke(dto, "missing"); } catch (Exception ignored) {}
        doReturn(null).when(service).getById("missing");

        assertThrows(RuntimeException.class, () -> service.updateBorrowRecord(dto));
        verify(service, never()).updateById(any());
    }

    @Test
    void updateBorrowRecord_invalidStatus_throws() {
        BorrowRecords existing = buildRecord("r2", "APPROVED");
        BorrowRecordsDTO dto = new BorrowRecordsDTO();
        try { dto.getClass().getMethod("setRecordId", String.class).invoke(dto, "r2"); } catch (Exception ignored) {}
        doReturn(existing).when(service).getById("r2");

        assertThrows(RuntimeException.class, () -> service.updateBorrowRecord(dto));
        verify(service, never()).updateById(any());
    }

    @Test
    void approveBorrowRecord_success_and_failures() {
        BorrowRecords pending = buildRecord("a1", "PENDING");
        doReturn(pending).when(service).getById("a1");
        doReturn(true).when(service).updateById(any(BorrowRecords.class));

        boolean ok = service.approveBorrowRecord("a1", "ok");
        assertTrue(ok);
        assertEquals("APPROVED", pending.getStatus());
        assertEquals("ok", pending.getAdminNotes());

        // not found
        doReturn(null).when(service).getById("no");
        assertThrows(RuntimeException.class, () -> service.approveBorrowRecord("no", "n"));

        // wrong status
        BorrowRecords notPending = buildRecord("a2", "APPROVED");
        doReturn(notPending).when(service).getById("a2");
        assertThrows(RuntimeException.class, () -> service.approveBorrowRecord("a2", "n"));
    }

    @Test
    void rejectBorrowRecord_success_and_failures() {
        BorrowRecords pending = buildRecord("b1", "PENDING");
        doReturn(pending).when(service).getById("b1");
        doReturn(true).when(service).updateById(any(BorrowRecords.class));

        boolean ok = service.rejectBorrowRecord("b1", "nope");
        assertTrue(ok);
        assertEquals("REJECTED", pending.getStatus());
        assertEquals("nope", pending.getAdminNotes());

        doReturn(null).when(service).getById("nx");
        assertThrows(RuntimeException.class, () -> service.rejectBorrowRecord("nx", "x"));

        BorrowRecords notPending = buildRecord("b2", "REJECTED");
        doReturn(notPending).when(service).getById("b2");
        assertThrows(RuntimeException.class, () -> service.rejectBorrowRecord("b2", "x"));
    }

    @Test
    void cancelBorrowRecord_success_and_invalid() {
        BorrowRecords p = buildRecord("c1", "PENDING");
        doReturn(p).when(service).getById("c1");
        doReturn(true).when(service).updateById(any(BorrowRecords.class));
        assertTrue(service.cancelBorrowRecord("c1"));
        assertEquals("CANCELLED", p.getStatus());

        BorrowRecords a = buildRecord("c2", "APPROVED");
        doReturn(a).when(service).getById("c2");
        assertTrue(service.cancelBorrowRecord("c2"));
        assertEquals("CANCELLED", a.getStatus());

        BorrowRecords other = buildRecord("c3", "BORROWED");
        doReturn(other).when(service).getById("c3");
        assertThrows(RuntimeException.class, () -> service.cancelBorrowRecord("c3"));
    }

    @Test
    void cancelBorrowRecord_notFound_throws() {
        doReturn(null).when(service).getById("missing");
        assertThrows(RuntimeException.class, () -> service.cancelBorrowRecord("missing"));
    }
    @Test
    void borrowAsset_success_and_failures() {
        BorrowRecords rec = buildRecord("d1", "APPROVED");
        doReturn(rec).when(service).getById("d1");
        doReturn(true).when(service).updateById(any(BorrowRecords.class));

        assertTrue(service.borrowAsset("d1"));
        assertEquals("BORROWED", rec.getStatus());
        assertNotNull(rec.getActualBorrowDate());

        doReturn(null).when(service).getById("nx");
        assertThrows(RuntimeException.class, () -> service.borrowAsset("nx"));

        BorrowRecords wrong = buildRecord("d2", "PENDING");
        doReturn(wrong).when(service).getById("d2");
        assertThrows(RuntimeException.class, () -> service.borrowAsset("d2"));
    }

    @Test
    void returnAsset_success_and_failures() {
        BorrowRecords rec = buildRecord("e1", "BORROWED");
        doReturn(rec).when(service).getById("e1");
        doReturn(true).when(service).updateById(any(BorrowRecords.class));

        assertTrue(service.returnAsset("e1"));
        assertEquals("RETURNED", rec.getStatus());
        assertNotNull(rec.getActualReturnDate());

        doReturn(null).when(service).getById("nx2");
        assertThrows(RuntimeException.class, () -> service.returnAsset("nx2"));

        BorrowRecords wrong = buildRecord("e2", "PENDING");
        doReturn(wrong).when(service).getById("e2");
        assertThrows(RuntimeException.class, () -> service.returnAsset("e2"));
    }

    @Test
    void getBorrowRecordsPage_and_convert() {
        BorrowRecords r1 = buildRecord("p1", "PENDING");
        BorrowRecords r2 = buildRecord("p2", "BORROWED");

        Page<BorrowRecords> recordsPage = new Page<>(1, 10);
        recordsPage.setRecords(Arrays.asList(r1, r2));
        recordsPage.setTotal(2);

        // stub page(...) to return our page
        doReturn(recordsPage).when(service).page(any(Page.class), any());

        BorrowRecordsQueryDTO q = new BorrowRecordsQueryDTO();
        try {
            q.getClass().getMethod("setPageNum", Integer.class).invoke(q, 1);
            q.getClass().getMethod("setPageSize", Integer.class).invoke(q, 10);
        } catch (Exception ignored) {}

        Page<BorrowRecordsVO> voPage = service.getBorrowRecordsPage(q);
        assertEquals(2, voPage.getRecords().size());
        // statusDesc should be set
        assertNotNull(voPage.getRecords().get(0).getStatusDesc());
    }

    @Test
    void getBorrowRecordDetail_and_userRecords_and_overdue() {
        BorrowRecords found = buildRecord("z1", "RETURNED");
        doReturn(found).when(service).getById("z1");
        assertNotNull(service.getBorrowRecordDetail("z1"));

        doReturn(null).when(service).getById("missing");
        assertNull(service.getBorrowRecordDetail("missing"));

        // user records
        BorrowRecords ur = buildRecord("u1", "BORROWED");
        doReturn(Collections.singletonList(ur)).when(service).list((Wrapper<BorrowRecords>) any());
        List<BorrowRecordsVO> list = service.getUserBorrowRecords("user1");
        assertEquals(1, list.size());

        // overdue
        BorrowRecords od = buildRecord("o1", "BORROWED");
        doReturn(Collections.singletonList(od)).when(service).list((Wrapper<BorrowRecords>) any());
        List<BorrowRecordsVO> overdue = service.getOverdueRecords();
        assertEquals(1, overdue.size());
    }

    @Test
    void isAssetAvailable_true_and_false() {
        // available when count == 0
        doReturn(0L).when(service).count(any());
        boolean avail = service.isAssetAvailable("a", new Date(), new Date());
        assertTrue(avail);

        doReturn(2L).when(service).count(any());
        boolean notAvail = service.isAssetAvailable("a", new Date(), new Date());
        assertFalse(notAvail);
    }

    @Test
    void getStartOfDay_and_getEndOfDay_should_return_day_bounds() {
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.NOVEMBER, 20, 15, 30, 45);
        cal.set(Calendar.MILLISECOND, 123);
        Date sample = cal.getTime();

        Date start = service.getStartOfDay(sample);
        Calendar s = Calendar.getInstance();
        s.setTime(start);
        assertEquals(0, s.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, s.get(Calendar.MINUTE));
        assertEquals(0, s.get(Calendar.SECOND));
        assertEquals(0, s.get(Calendar.MILLISECOND));

        Date end = service.getEndOfDay(sample);
        Calendar e = Calendar.getInstance();
        e.setTime(end);
        assertEquals(23, e.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, e.get(Calendar.MINUTE));
        assertEquals(59, e.get(Calendar.SECOND));
        assertEquals(999, e.get(Calendar.MILLISECOND));
    }

    @Test
    void buildQueryWrapper_should_produce_wrapper_when_fields_set() {
        BorrowRecordsQueryDTO q = new BorrowRecordsQueryDTO();
        q.setUserId("user-x");
        q.setAssetId("asset-y");
        q.setStatus("PENDING");
        // set some date bounds to exercise those branches
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1);
        q.setApplyDateStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        q.setApplyDateEnd(cal.getTime());
        q.setPageNum(1);
        q.setPageSize(10);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        q.setPlannedBorrowDateStart(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        q.setPlannedBorrowDateEnd(cal.getTime());
        // prepare a stubbed page return so getBorrowRecordsPage invokes page(...) and we can capture the wrapper
        Page<BorrowRecords> stubPage = new Page<>(1, 10);
        doReturn(stubPage).when(service).page(any(Page.class), any());

        service.getBorrowRecordsPage(q);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<LambdaQueryWrapper> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(service).page(any(Page.class), captor.capture());

        LambdaQueryWrapper<?> wrapper = captor.getValue();
        assertNotNull(wrapper, "buildQueryWrapper 应返回非空的 LambdaQueryWrapper");
        // 基本断言：类型正确并且实例存在
        assertTrue(wrapper.getClass().getName().toLowerCase().contains("lambdaquerywrapper"));
    }

    @Test
    void buildQueryWrapper_with_empty_fields_should_still_return_wrapper() {
        BorrowRecordsQueryDTO q = new BorrowRecordsQueryDTO();
        q.setPageNum(1);
        q.setPageSize(5);

        Page<BorrowRecords> stubPage = new Page<>(1, 5);
        doReturn(stubPage).when(service).page(any(Page.class), any());

        service.getBorrowRecordsPage(q);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<LambdaQueryWrapper> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(service).page(any(Page.class), captor.capture());

        LambdaQueryWrapper<?> wrapper = captor.getValue();
        assertNotNull(wrapper);
    }

}
