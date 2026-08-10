package com.arishi.AXAM.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ExamSchedulerRequest {

    @NotBlank(message = "Exam title is required")
    private String examTitle;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;
}