package com.arishi.AXAM.dto.request;


import com.arishi.AXAM.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Category title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Size(max = 250, message = "Description max length 250")
    private String description;

    private CategoryStatus status;
}
