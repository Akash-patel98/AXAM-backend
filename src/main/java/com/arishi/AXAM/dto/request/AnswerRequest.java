package com.arishi.AXAM.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    @NotNull(message = "Question number is required")
    private Integer questionNumber;
    @NotNull(message = "Selected answer is required")
    @Pattern(regexp = "[ABCD]|null", message = "Answer must be A, B, C, D, or null (for unanswered)")
    private String selectedAnswer;

    private Long timeSpentInSeconds;
}
