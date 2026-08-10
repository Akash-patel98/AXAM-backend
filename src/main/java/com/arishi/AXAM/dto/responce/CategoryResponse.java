package com.arishi.AXAM.dto.responce;


import com.arishi.AXAM.enums.CategoryStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private Long id;

    private String title;

    private String description;

    private CategoryStatus status;
}
