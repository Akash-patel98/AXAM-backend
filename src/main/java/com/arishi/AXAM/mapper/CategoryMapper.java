package com.arishi.AXAM.mapper;


import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;
import com.arishi.AXAM.enums.CategoryStatus;
import com.arishi.AXAM.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {


    public Category toEntity(CategoryRequest request) {

        return Category.builder().title(request.getTitle()).description(request.getDescription()).status(request.getStatus() != null ? request.getStatus() : CategoryStatus.ACTIVE).build();
    }


    public CategoryResponse toResponse(Category category) {

        return CategoryResponse.builder().id(category.getId()).title(category.getTitle()).description(category.getDescription()).status(category.getStatus()).build();
    }


    public void updateEntity(Category category, CategoryRequest request) {

        category.setTitle(request.getTitle());
        category.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
    }
}