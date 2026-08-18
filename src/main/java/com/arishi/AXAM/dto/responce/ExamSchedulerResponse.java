package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.ExamSchedulerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSchedulerResponse {

    private Long id;

    private Long examId;

    private String examTitle;

    private ExamSchedulerStatus status;

    private Instant startDate;

    private Instant endDate;

    private String title;

    private String description;

    private Integer maxAttempts;
}