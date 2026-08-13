package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.ExamAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStartResponse {

    private Long attemptId;

    private Long examId;

    private String examTitle;

    private String instruction;

    private Integer totalQuestions;

    private Integer totalMarks;

    private Integer attemptedQuestions;

    private Integer unattemptedQuestions;

    private Instant startAt;

    private Instant endAt;

    private Long remainingSeconds;

    private ExamAttemptStatus status;

    private List<AttemptQuestionResponse> questions;
}