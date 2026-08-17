package com.arishi.AXAM.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamResponse {

    private Long attemptId;                    // Exam attempt record ID
    private String activeSessionId;            // UUID for session tracking
    private Integer totalQuestions;            // Total questions in exam
    private Integer duration;                  // Exam duration in minutes
    private LocalDateTime startTime;           // When exam started
    private LocalDateTime endTime;             // When exam expires (startTime + duration)
    private List<ExamQuestionDTO> questions;   // 50 questions without correct answer

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExamQuestionDTO {

        private Long id;

        private Integer displayOrder;

        private String questionContent;

        private String optionA;

        private String optionB;

        private String optionC;

        private String optionD;

        private String difficultyLevel;

        private String imageUrl;
    }
}
