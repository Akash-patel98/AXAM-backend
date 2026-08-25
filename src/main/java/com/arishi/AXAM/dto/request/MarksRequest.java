package com.arishi.AXAM.dto.request;

import com.arishi.AXAM.enums.DifficultyLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarksRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "Marks value is required")
    @Positive(message = "Marks must be greater than zero")
    private Integer marks;
}