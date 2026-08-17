package com.arishi.AXAM.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCsvError {

    private long rowNumber;

    private String questionContent;

    private String reason;
}