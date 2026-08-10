package com.arishi.AXAM.dto.responce;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.enums.QuestionsStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class QuestionResponse {

    private Long id;

    private String category;

    private String questionContent;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    private String correctAnswer;

    private String imageUrl;

    private QuestionsStatus status;
}