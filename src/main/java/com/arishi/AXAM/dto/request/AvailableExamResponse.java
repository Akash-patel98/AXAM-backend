package com.arishi.AXAM.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableExamResponse {

    private Long examId;
    private String title;
    private String description;
    private String instruction;
    private Integer duration;          // minutes
    private Float passingPercentage;

    private Long schedulerId;
    private Instant startDate;
    private Instant endDate;

    private Integer maxAttempts;
    private Integer attemptsUsed;
    private Integer attemptsRemaining;
}