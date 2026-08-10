package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;
import com.arishi.AXAM.enums.CategoryStatus;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.CategoryMapper;
import com.arishi.AXAM.model.Category;
import com.arishi.AXAM.repo.CategoryRepository;
import com.arishi.AXAM.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;


    @Override
    public CategoryResponse create(CategoryRequest request) {

        // 1 Check duplicate category title and case In sensitive , Java==java

        if (categoryRepository.existsByTitleIgnoreCaseAndDeletedAtIsNull(request.getTitle())) {
            throw new DuplicateResourceException("Category already exists");
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategoryByTitle(String title) {

        Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(title).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategory() {

        // GEt All active caategories and deleted is null
        List<Category> categories = categoryRepository.findByStatusAndDeletedAtIsNull(CategoryStatus.ACTIVE);

        List<CategoryResponse> responses = new ArrayList<>();

        for (Category category : categories) responses.add(categoryMapper.toResponse(category));

        return responses;
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        return null;
    }

    @Override
    public void deleteCategoryByID(Long id) {

    }

}
