package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.enums.DifficultyLevel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request, MultipartFile image);

    List<QuestionResponse> getAllQuestions();


    List<QuestionResponse> getQuestionsByCategory(String categoryTitle);

    List<QuestionResponse> getQuestionsByCategoryAndDifficulty(
            String categoryTitle,
            DifficultyLevel difficultyLevel);
}