package com.arishi.AXAM.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamRequest {

    @NotBlank(message = "Exam title is required")
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String title;

    @Size(max = 250, message = "Description max length 250")
    private String description;

    @Size(max = 500, message = "Instruction max length 500")
    private String instruction;

    @NotBlank(message = "Blueprint title is required")
    private String blueprintTitle;

    @NotNull(message = "Passing percentage is required")
    @DecimalMin(value = "0.0", message = "Passing percentage cannot be less than 0")
    @DecimalMax(value = "100.0", message = "Passing percentage cannot be greater than 100")
    private Float passingPercentage;

    @NotNull(message = "Duration is required")
    @jakarta.validation.constraints.Positive(message = "Duration must be greater than 0")
    private Integer duration;
}