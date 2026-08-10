package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestPart("question") QuestionRequest request, @RequestPart(value = "image", required = false) MultipartFile image) {

        QuestionResponse response = questionService.createQuestion(request, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/questions/all")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {

        List<QuestionResponse> response = questionService.getAllQuestions();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/questions/category")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCategory(@RequestParam String categoryTitle) {

        List<QuestionResponse> responses = questionService.getQuestionsByCategory(categoryTitle);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/questions/filter")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCategoryAndDifficulty(@RequestParam String category, @RequestParam DifficultyLevel difficultyLevel) {

        List<QuestionResponse> responses = questionService.getQuestionsByCategoryAndDifficulty(category, difficultyLevel);

        return ResponseEntity.ok(responses);
    }
}