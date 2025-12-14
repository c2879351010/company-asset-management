package com.example.backend.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 備品マスター(Assets)表实体类
 *
 * @author makejava
 * @since 2025-11-04 19:35:19
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class Assets extends Model<Assets> {

//備品一意識別子
    @TableId
    private String assetId;
//備品名称
    private String name;
//備品説明
    private String description;
//状態
    private String status;
//購入日
    private Date purchaseDate;
//画像ファイルパス
    private String imageUrl;
//分類
    private String categories;


}

