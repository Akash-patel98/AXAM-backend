package com.arishi.AXAM.dto.request.filter;

import com.arishi.AXAM.enums.CategoryStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryFilterRequest extends BaseFilterRequest {

    private String title;

    private CategoryStatus status;


}