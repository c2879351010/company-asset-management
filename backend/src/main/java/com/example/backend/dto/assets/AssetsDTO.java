package com.example.backend.dto.assets;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AssetsDTO {

    private String assetId;

    @NotBlank(message = "备品名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "状态不能为空")
    private String status;

    private Date purchaseDate;

    private String imageUrl;

    @NotBlank(message = "分类不能为空")
    private String categories;
}