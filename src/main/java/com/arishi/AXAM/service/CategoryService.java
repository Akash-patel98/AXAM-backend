package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.filter.CategoryFilterRequest;
import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;
import com.arishi.AXAM.dto.responce.PageResponse;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse getCategoryByTitle(String title);


    PageResponse<CategoryResponse> searchCategories(CategoryFilterRequest request);

    void deleteCategoryByID(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest request);
}