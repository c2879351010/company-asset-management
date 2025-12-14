package com.example.backend.dto.assets;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AssetsVO {

    private String assetId;
    private String name;
    private String description;
    private String status;
    private Date purchaseDate;
    private String imageUrl;
    private String categories;

}