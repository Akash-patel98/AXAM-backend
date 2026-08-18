package com.arishi.AXAM.controller;

import com.arishi.AXAM.dto.ApiResponse;
import com.arishi.AXAM.dto.request.filter.CategoryFilterRequest;
import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;
import com.arishi.AXAM.dto.responce.PageResponse;
import com.arishi.AXAM.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    // CREATE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Category created successfully", response));
    }


    // GET ALL ACTIVE CATEGORIES
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> searchCategories(@RequestBody CategoryFilterRequest request) {

        PageResponse<CategoryResponse> response = categoryService.searchCategories(request);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Categories fetched successfully", response));
    }

    // GET CATEGORY BY TITLE
    @GetMapping("/title/{title}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByTitle(@PathVariable String title) {

        CategoryResponse response = categoryService.getCategoryByTitle(title);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category fetched successfully", response));
    }


    // UPDATE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category updated successfully", response));
    }


    // DELETE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategoryByID(id);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category deleted successfully", null));
    }
}