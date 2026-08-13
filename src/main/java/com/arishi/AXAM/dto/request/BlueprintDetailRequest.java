package com.arishi.AXAM.dto.request;

import com.arishi.AXAM.enums.DifficultyLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor

@NoArgsConstructor
@Data
public class BlueprintDetailRequest {

    @NotNull(message = "Category is required")
    private String categoryTitle;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "Question count is required")
    @Positive(message = "Question count must be greater than 0")
    private Integer questionCount;
}
