package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.model.Question;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {

        return Question.builder()
                .questionContent(request.getQuestionContent())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .difficultyLevel(request.getDifficultyLevel())
                .correctAnswer(request.getCorrectAnswer())
                .status(request.getStatus())
                .build();
    }

    public QuestionResponse toResponse(Question question) {

        return QuestionResponse.builder()
                .id(question.getId())
                .category(question.getCategory().getTitle())
                .questionContent(question.getQuestionContent())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .difficultyLevel(question.getDifficultyLevel())
                .correctAnswer(question.getCorrectAnswer())
                .status(question.getStatus())
                .imageUrl(question.getImageUrl())
                .build();
    }
}