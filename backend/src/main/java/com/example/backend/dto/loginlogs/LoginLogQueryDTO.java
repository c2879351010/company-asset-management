package com.example.backend.dto.loginlogs;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogQueryDTO {

    private String userId;

    private String operationResult;

    private Date startTime;

    private Date endTime;

    private Long current = 1L;

    private Long size = 10L;

}
