package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.ExamAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAttemptSummaryResponse {

    private Long attemptId;

    private Long examId;
    private String examTitle;

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    private ExamAttemptStatus status;

    private Instant startAt;
    private Instant endAt;

    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer obtainedMarks;
    private Integer totalMarks;
    private Float percentage;
}