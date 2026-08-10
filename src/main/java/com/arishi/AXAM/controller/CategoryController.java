package com.arishi.AXAM.controller;


import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;
import com.arishi.AXAM.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController { //

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Category created successfully", response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategory() {

        List<CategoryResponse> response = categoryService.getAllCategory();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Categories fetched successfully", response));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByTitle(@PathVariable String title) {

        CategoryResponse response = categoryService.getCategoryByTitle(title);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category fetched successfully", response));
    }


}
