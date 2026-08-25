package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarksResponse {
    private Long id;
    private Long examId;
    private DifficultyLevel difficultyLevel;
    private Integer marks;
}