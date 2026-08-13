package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse getCategoryByTitle(String title);

    List<CategoryResponse> getAllCategory();

    void deleteCategoryByID(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest request);
}