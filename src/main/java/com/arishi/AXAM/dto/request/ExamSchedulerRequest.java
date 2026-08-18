package com.arishi.AXAM.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSchedulerRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotBlank(message = "Schedule title is required")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    private Instant startDate;

    @NotNull(message = "End date is required")
    private Instant endDate;

    @NotNull(message = "Max attempts is required")
    private Integer maxAttempts;
}