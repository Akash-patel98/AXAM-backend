package com.arishi.AXAM.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptQuestionResponse {

    private Long attemptQuestionId;

    private Long questionId;

    private String questionContent;

    private String imageUrl;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    private Boolean answered;

    private String selectedAnswer;

    private Integer displayOrder;
}