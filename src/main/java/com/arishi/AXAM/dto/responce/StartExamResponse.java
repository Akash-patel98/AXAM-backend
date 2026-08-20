package com.arishi.AXAM.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamResponse {

    private Long attemptId;
    private String activeSessionId;
    private Integer totalQuestions;
    private Integer duration;
    private Instant startTime;
    private Instant endTime;
    private List<ExamQuestionDTO> questions;

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
