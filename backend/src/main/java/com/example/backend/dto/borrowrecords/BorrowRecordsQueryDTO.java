package com.example.backend.dto.borrowrecords;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordsQueryDTO {

    private String userId;

    private String assetId;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyDateEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedBorrowDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedBorrowDateEnd;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

}
