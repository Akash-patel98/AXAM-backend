package com.arishi.AXAM.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCsvUploadResponse {

    private int totalRows;

    private int successCount;

    private int failedCount;

    private List<QuestionCsvError> errors;
}

