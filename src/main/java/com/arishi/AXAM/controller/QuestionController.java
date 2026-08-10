package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//@RestController
//@RequestMapping("/api/v1/questions")
//@RequiredArgsConstructor
//public class QuestionController {
//
//    private final QuestionService questionService;
//
//    @PostMapping("/questions")
//   // public ResponseEntity<QuestionResponse> createQuestion(@RequestPart QuestionRequest request, @RequestPart(required = false) MultipartFile image) {
//    public ResponseEntity<QuestionResponse> createQuestion(@RequestPart QuestionRequest request) {
//
//        MultipartFile image = null;
//        QuestionResponse response = questionService.createQuestion(request, image);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//}

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(value = "/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestPart("question") QuestionRequest request, @RequestPart(value = "image", required = false) MultipartFile image) {

        System.out.println(image);
        QuestionResponse response = questionService.createQuestion(request, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}