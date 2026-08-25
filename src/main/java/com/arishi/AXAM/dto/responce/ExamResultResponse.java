package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.dto.responce.SubmitExamResponse.QuestionResultDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {

    private Long attemptId;

    private Long examId;
    private String examName;

    private String attemptStatus;

    private Integer totalQuestions;
    private Integer attemptedQuestions;
    private Integer unattemptedQuestions;

    private Integer correctAnswers;
    private Integer incorrectAnswers;

    private Integer obtainedMarks;
    private Integer totalMarks;

    private Float percentage;

    private String resultStatus;

    private List<QuestionResultDTO> questionResults;
}