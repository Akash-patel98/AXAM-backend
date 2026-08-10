package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.BluePrintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BluePrintResponse {

    private Long id;

    private String title;

    private String description;

    private BluePrintStatus status;

    private List<BluePrintDetailResponse> details;
}