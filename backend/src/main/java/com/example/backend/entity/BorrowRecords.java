package com.example.backend.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
/**
 * 貸出記録(BorrowRecords)表实体类
 *
 * @author makejava
 * @since 2025-11-04 19:35:21
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class BorrowRecords extends Model<BorrowRecords> {
//貸出記録一意識別子
    @TableId
    private String recordId;
//申請ユーザー参照
    private String userId;
//貸出備品参照
    private String assetId;
//申請日時
    private Date applyDate;
//計画貸出日
    private Date plannedBorrowDate;
//計画返却日
    private Date plannedReturnDate;
//実際の貸出日
    private Date actualBorrowDate;
//実際の返却日
    private Date actualReturnDate;
//状態
    private String status;
//貸出目的
    private String purpose;
//管理者備考
    private String adminNotes;
//作成日時
    private Date createdAt;
//更新日時
    private Date updatedAt;

}

