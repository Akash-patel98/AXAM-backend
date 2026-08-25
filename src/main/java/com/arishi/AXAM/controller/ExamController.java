package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;
import com.arishi.AXAM.dto.responce.ExamStartResponse;
import com.arishi.AXAM.enums.ExamStatus;
import com.arishi.AXAM.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@Valid @RequestBody ExamRequest request) {

        ExamResponse response = examService.createExam(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Exam created successfully", response));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getAllExams() {

        List<ExamResponse> response = examService.getAllExams();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exams fetched successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ExamResponse>> getExamByTitle(@RequestParam String title) {

        ExamResponse response = examService.getExamByTitle(title);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam fetched successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ExamResponse>> updateStatus(@PathVariable Long id, @RequestParam ExamStatus status) {

        ExamResponse response = examService.updateStatus(id, status);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam status updated successfully", response));
    }

}