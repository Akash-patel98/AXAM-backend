package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.responce.AvailableExamResponse;
import com.arishi.AXAM.dto.responce.CandidateExamResponse;
import com.arishi.AXAM.dto.responce.ExamResultResponse;
import com.arishi.AXAM.service.CandidateExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateExamController {

    private final CandidateExamService candidateExamService;

    @GetMapping("/my-exams")
    public ResponseEntity<ApiResponse<List<CandidateExamResponse>>> getMyExams() {

        List<CandidateExamResponse> response = candidateExamService.getMyExams();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "My exams fetched successfully", response));
    }

    @GetMapping("/exam-result/{attemptId}")
    public ResponseEntity<ApiResponse<ExamResultResponse>> getExamResult(@PathVariable Long attemptId) {

        ExamResultResponse response = candidateExamService.getExamResult(attemptId);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam result fetched successfully", response));
    }

    @GetMapping("/available-exams")
    public ResponseEntity<ApiResponse<List<AvailableExamResponse>>> getAvailableExams() {

        List<AvailableExamResponse> response = candidateExamService.getAvailableExams();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Available exams fetched successfully", response));
    }
}