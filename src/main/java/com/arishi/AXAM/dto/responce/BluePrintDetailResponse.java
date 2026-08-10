package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.DifficultyLevel;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BluePrintDetailResponse {

    private Long id;

    private Long categoryId;

    private String categoryTitle;

    private DifficultyLevel difficultyLevel;

    private Integer questionCount;
}