package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.ExamSchedulerRequest;
import com.arishi.AXAM.dto.responce.ExamSchedulerResponse;
import com.arishi.AXAM.enums.ExamSchedulerStatus;
import com.arishi.AXAM.service.ExamSchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam-schedulers")
@RequiredArgsConstructor
public class ExamSchedulerController {

    private final ExamSchedulerService examSchedulerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ExamSchedulerResponse>> createScheduler(@Valid @RequestBody ExamSchedulerRequest request) {

        ExamSchedulerResponse response = examSchedulerService.createScheduler(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Exam scheduled successfully", response));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamSchedulerResponse>>> getAllSchedulers() {

        List<ExamSchedulerResponse> response = examSchedulerService.getAllSchedulers();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam schedulers fetched successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamSchedulerResponse>> getSchedulerById(@PathVariable Long id) {

        ExamSchedulerResponse response = examSchedulerService.getSchedulerById(id);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam scheduler fetched successfully", response));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ExamSchedulerResponse>> updateStatus(@PathVariable Long id, @RequestParam ExamSchedulerStatus status) {

        ExamSchedulerResponse response = examSchedulerService.updateStatus(id, status);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Exam scheduler status updated successfully", response));
    }
}