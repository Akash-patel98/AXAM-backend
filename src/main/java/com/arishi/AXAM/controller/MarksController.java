package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.MarksRequest;
import com.arishi.AXAM.dto.responce.MarksResponse;
import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.service.MarksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marks")
@RequiredArgsConstructor
public class MarksController {

    private final MarksService marksService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MarksResponse>> createMarks(@Valid @RequestBody MarksRequest request) {

        MarksResponse response = marksService.createMarks(request);

        return ResponseEntity.ok(ApiResponse.success(200, "Marks configured successfully", response));
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MarksResponse>>> getAllMarksForExam(@PathVariable Long examId) {

        List<MarksResponse> responses = marksService.getAllMarksForExam(examId);

        return ResponseEntity.ok(ApiResponse.success(200, "Marks fetched successfully", responses));
    }

    @GetMapping("/exam/{examId}/difficulty/{difficultyLevel}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MarksResponse>> getMarks(@PathVariable Long examId, @PathVariable DifficultyLevel difficultyLevel) {

        MarksResponse response = marksService.getMarks(examId, difficultyLevel);

        return ResponseEntity.ok(ApiResponse.success(200, "Marks fetched successfully", response));
    }

    @DeleteMapping("/{marksId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMarks(@PathVariable Long marksId) {

        marksService.deleteMarks(marksId);

        return ResponseEntity.ok(ApiResponse.success(200, "Marks configuration deleted successfully", null));
    }
}