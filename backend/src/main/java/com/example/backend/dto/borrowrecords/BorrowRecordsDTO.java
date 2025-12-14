package com.example.backend.dto.borrowrecords;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class BorrowRecordsDTO {
    private String recordId;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "资产ID不能为空")
    private String assetId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyDate;

    @NotNull(message = "计划借出日期不能为空")
    @Future(message = "计划借出日期必须是未来时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedBorrowDate;

    @NotNull(message = "计划归还日期不能为空")
    @Future(message = "计划归还日期必须是未来时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date plannedReturnDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualBorrowDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualReturnDate;

    private String status;

    private String purpose;

    private String adminNotes;

}
