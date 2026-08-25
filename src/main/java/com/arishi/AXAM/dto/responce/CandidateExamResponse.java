package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.ExamAttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class CandidateExamResponse {

    private Long examId;

    private Long attemptId;

    private String examTitle;

    private Instant startAt;

    private Instant endAt;

    private Integer totalQuestions;

    private ExamAttemptStatus status;
}