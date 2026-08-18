package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.request.filter.QuestionFilterRequest;
import com.arishi.AXAM.dto.responce.PageResponse;
import com.arishi.AXAM.dto.responce.QuestionCsvUploadResponse;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // CREATE QUESTION
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@Valid @RequestPart("question") QuestionRequest request, @RequestPart(value = "image", required = false) MultipartFile image) {

        QuestionResponse response = questionService.createQuestion(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Question created successfully", response));
    }

    // SEARCH / FILTER QUESTIONS (changed to POST, consistent with category)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> searchQuestions(@RequestBody QuestionFilterRequest questionFilterRequest) {

        PageResponse<QuestionResponse> response = questionService.searchQuestions(questionFilterRequest);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Questions fetched successfully", response));
    }

    // UPDATE QUESTION
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(@PathVariable Long id, @Valid @RequestPart("question") QuestionRequest request, @RequestPart(value = "image", required = false) MultipartFile image) {

        QuestionResponse response = questionService.updateQuestion(id, request, image);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Question updated successfully", response));
    }

    // DELETE QUESTION
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {

        questionService.deleteQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Question deleted successfully", null));
    }

    // DOWNLOAD SAMPLE CSV (file download – no wrapper)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/csv/sample")
    public ResponseEntity<Resource> downloadSampleCsv() {

        Resource resource = questionService.downloadSampleCsv();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=questions_sample.csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }

    // UPLOAD QUESTIONS CSV (now returns ApiResponse<Void>)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<QuestionCsvUploadResponse>> uploadQuestionsCsv(@RequestParam("file") MultipartFile file) {
        QuestionCsvUploadResponse result = questionService.uploadQuestionsCsv(file);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "CSV processed successfully", result));
    }


    // DOWNLOAD ALL QUESTIONS CSV (file download – no wrapper)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/csv/download")
    public ResponseEntity<Resource> downloadQuestionsCsv() {

        Resource resource = questionService.downloadQuestionsCsv();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=questions.csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }
}