package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.ExamSchedularStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ExamSchedulerResponse {

    private Long id;

    private Long examId;

    private String examTitle;

    private ExamSchedularStatus status;

    private Instant startTime;

    private Instant endTime;
}