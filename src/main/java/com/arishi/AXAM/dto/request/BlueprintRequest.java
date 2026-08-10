package com.arishi.AXAM.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlueprintRequest {

    @NotBlank(message = "Blueprint title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Size(max = 250, message = "Description max length 250")
    private String description;

    @Valid
    private List<BlueprintDetailRequest> details;
}