package com.arishi.AXAM.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartAttemptRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Scheduler ID is required")
    private Long schedulerId;
}
