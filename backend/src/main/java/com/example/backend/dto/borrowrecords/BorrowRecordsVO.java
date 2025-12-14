package com.example.backend.dto.borrowrecords;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordsVO {

    private String recordId;

    private String userId;

    private String userName;

    private String assetId;

    private String assetName;

    private String assetType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedBorrowDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedReturnDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualBorrowDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualReturnDate;

    private String status;

    private String statusDesc;

    private String purpose;

    private String adminNotes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

}
