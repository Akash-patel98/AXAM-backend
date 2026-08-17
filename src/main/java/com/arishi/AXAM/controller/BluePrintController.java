package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.BlueprintRequest;
import com.arishi.AXAM.dto.responce.BluePrintResponse;
import com.arishi.AXAM.service.BluePrintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/blueprints")
@RequiredArgsConstructor
public class BluePrintController {

    private final BluePrintService bluePrintService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BluePrintResponse>> createBlueprint(@Valid @RequestBody BlueprintRequest request) {

        BluePrintResponse response = bluePrintService.createBlueprint(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Blueprint created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BluePrintResponse>>> getAllBlueprints() {

        List<BluePrintResponse> response = bluePrintService.getAllBlueprints();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Blueprints fetched successfully", response));
    }

    // search by title
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<BluePrintResponse>> getBlueprintByTitle(@RequestParam String title) {

        BluePrintResponse response = bluePrintService.getBlueprintByTitle(title);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Blueprint fetched successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BluePrintResponse>> updateStatus(@PathVariable Long id, @RequestParam com.arishi.AXAM.enums.BluePrintStatus status) {

        BluePrintResponse response = bluePrintService.updateStatus(id, status);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Blueprint status updated successfully", response));
    }
}