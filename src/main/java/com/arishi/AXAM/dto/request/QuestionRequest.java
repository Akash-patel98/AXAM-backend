package com.arishi.AXAM.dto.request;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.enums.QuestionsStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class QuestionRequest {

    @NotNull(message = "Category is required")
    private String category;

    @NotBlank(message = "Question content is required")
    @Size(max = 1000, message = "Question content cannot exceed 1000 characters")
    private String questionContent;

    @NotBlank(message = "Option A is required")
    private String optionA;

    @NotBlank(message = "Option B is required")
    private String optionB;

    @NotBlank(message = "Option C is required")
    private String optionC;

    @NotBlank(message = "Option D is required")
    private String optionD;

    private MultipartFile image;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;


    private Boolean override = false;


    @Pattern(regexp = "[ABCD]", message = "correct answer must be A,B,C,D")
    private String correctAnswer;


    @NotNull(message = "Status is required")
    private QuestionsStatus status;
}