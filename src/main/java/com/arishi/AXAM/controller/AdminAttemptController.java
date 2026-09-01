package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.responce.AdminAttemptDetailResponse;
import com.arishi.AXAM.dto.responce.AdminAttemptSummaryResponse;
import com.arishi.AXAM.service.AdminAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/attempts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAttemptController {

    private final AdminAttemptService adminAttemptService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminAttemptSummaryResponse>>> getAllAttempts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startAt"));

        Page<AdminAttemptSummaryResponse> response = adminAttemptService.getAllAttempts(pageable);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Attempts fetched successfully", response));
    }

    @GetMapping("/by-exam/{examId}")
    public ResponseEntity<ApiResponse<Page<AdminAttemptSummaryResponse>>> getAttemptsByExam(@PathVariable Long examId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startAt"));

        Page<AdminAttemptSummaryResponse> response = adminAttemptService.getAttemptsByExam(examId, pageable);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Attempts fetched successfully", response));
    }

    @GetMapping("/by-candidate/{candidateId}")
    public ResponseEntity<ApiResponse<Page<AdminAttemptSummaryResponse>>> getAttemptsByCandidate(@PathVariable Long candidateId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startAt"));

        Page<AdminAttemptSummaryResponse> response = adminAttemptService.getAttemptsByCandidate(candidateId, pageable);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Attempts fetched successfully", response));
    }


    @GetMapping("/{attemptId}")
    public ResponseEntity<ApiResponse<AdminAttemptDetailResponse>> getAttemptDetail(@PathVariable Long attemptId) {

        AdminAttemptDetailResponse response = adminAttemptService.getAttemptDetail(attemptId);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Attempt detail fetched successfully", response));
    }
}