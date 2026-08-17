package com.arishi.AXAM.dto.responce;

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
public class SubmitExamResponse {

    private Long attemptId;

    private Integer totalQuestions;

    private Integer attemptedQuestions;

    private Integer unattemptedQuestions;

    private Integer correctAnswers;

    private Integer incorrectAnswers;

    private Integer obtainedMarks;

    private Integer totalMarks;

    private Float percentage;

    private String result;

    private Instant submittedAt;

    private List<QuestionResultDTO> questionResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResultDTO {

        private Integer displayOrder;

        private String questionContent;

        private String userAnswer;

        private String correctAnswer;

        private Boolean isCorrect;       // true/false

        private Integer marksObtained;   // 0 or 1

        private String difficultyLevel;  // EASY, MEDIUM, HARD

        private Long timeSpent;          // Seconds

        private String explanation;
    }
}
