package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.filter.CategoryFilterRequest;
import com.arishi.AXAM.dto.request.CategoryRequest;
import com.arishi.AXAM.dto.responce.CategoryResponse;
import com.arishi.AXAM.dto.responce.PageResponse;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.CategoryMapper;
import com.arishi.AXAM.model.Category;
import com.arishi.AXAM.repo.CategoryRepository;
import com.arishi.AXAM.service.CategoryService;
import com.arishi.AXAM.specification.CategorySpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
    public PageResponse<CategoryResponse> searchCategories(CategoryFilterRequest request) {

        //  build dynamic filters
        // only non-deleted categories
        // filter by title ,filter by status (if provided)
        Specification<Category> specification = Specification.where(CategorySpecification.isNotDeleted()).and(CategorySpecification.titleContains(request.getTitle())).and(CategorySpecification.hasStatus(request.getStatus()));

        // sort decide the order of categories
        Sort sort = request.getSortDir().equalsIgnoreCase("asc") ? Sort.by(request.getSortBy()).ascending() : Sort.by(request.getSortBy()).descending();

        // pagination , page , soze
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Category> categories = categoryRepository.findAll(specification, pageable);

        Page<CategoryResponse> mappedPage = categories.map(categoryMapper::toResponse);

        return new PageResponse<>(mappedPage.getContent(), mappedPage.getNumber(), mappedPage.getSize(), mappedPage.getTotalElements(), mappedPage.getTotalPages(), mappedPage.isFirst(), mappedPage.isLast());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {

        // Find category
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        // Check duplicate title
        boolean exists = categoryRepository.existsByTitleIgnoreCaseAndDeletedAtIsNullAndIdNot(request.getTitle(), id);

        if (exists) {
            throw new DuplicateResourceException("Category already exists: " + request.getTitle());
        }

        // Update
        category.setTitle(request.getTitle());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategoryByID(Long id) {

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        category.setDeletedAt(Instant.now());

        categoryRepository.save(category);
    }


}
