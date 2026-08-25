package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.AnswerRequest;
import com.arishi.AXAM.dto.request.StartAttemptRequest;
import com.arishi.AXAM.dto.responce.StartExamResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.security.CustomUserDetails;
import com.arishi.AXAM.service.ExamAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exam-attempts")
@RequiredArgsConstructor
@Slf4j
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

    @PreAuthorize("hasAnyRole('CANDIDATE', 'ADMIN')")
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartExamResponse>> startExamAttempt(@Valid @RequestBody StartAttemptRequest request) {

        Long userId = getCurrentUserId();
        StartExamResponse response = examAttemptService.startAttempt(request, userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam started successfully", response));
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'ADMIN')")
    @PostMapping("/{attemptId}/answer")
    public ResponseEntity<ApiResponse<Void>> submitAnswer(@PathVariable Long attemptId, @Valid @RequestBody AnswerRequest request, @RequestHeader("X-Session-ID") String sessionId) {

        Long userId = getCurrentUserId();
        examAttemptService.submitAnswer(attemptId, sessionId, request, userId);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Answer submitted successfully"));
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'ADMIN')")
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ApiResponse<SubmitExamResponse>> submitExam(@PathVariable Long attemptId, @RequestHeader("X-Session-ID") String sessionId) {

        Long userId = getCurrentUserId();
        SubmitExamResponse response = examAttemptService.submitExam(attemptId, sessionId, userId);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam submitted successfully", response));
    }


    @PreAuthorize("hasAnyRole('CANDIDATE', 'ADMIN')")
    @PostMapping("/{attemptId}/abandon")
    public ResponseEntity<ApiResponse<Void>> abandonExam(@PathVariable Long attemptId, @RequestHeader("X-Session-ID") String sessionId) {

        Long userId = getCurrentUserId();
        examAttemptService.abandonExam(attemptId, sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam abandoned"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser().getId();
        }

        throw new RuntimeException("Unexpected authentication principal type: " + principal.getClass());
    }
}