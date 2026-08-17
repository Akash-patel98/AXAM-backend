package com.arishi.AXAM.dto.responce;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {

    private Long id;

    private String title;

    private String description;

    private String instruction;

    private Float passingPercentage;

    private Integer duration;

    private com.arishi.AXAM.enums.ExamStatus status;

    private Long blueprintId;

    private String blueprintTitle;
}