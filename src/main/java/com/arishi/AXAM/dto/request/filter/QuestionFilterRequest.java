package com.arishi.AXAM.dto.request.filter;

import com.arishi.AXAM.enums.DifficultyLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionFilterRequest extends BaseFilterRequest {

    private String category;

    private DifficultyLevel difficultyLevel;
}