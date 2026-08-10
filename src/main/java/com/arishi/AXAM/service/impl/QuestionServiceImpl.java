package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.QuestionMapper;
import com.arishi.AXAM.model.Category;
import com.arishi.AXAM.model.Question;
import com.arishi.AXAM.repo.CategoryRepo;
import com.arishi.AXAM.repo.QuestionRepository;
import com.arishi.AXAM.service.QuestionService;
import com.arishi.AXAM.service.FileStorageService;
import com.arishi.AXAM.util.QuestionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {


    private final CategoryRepo categoryRepo;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final FileStorageService fileStorageService;

    @Override
    public QuestionResponse createQuestion(QuestionRequest request, MultipartFile image) {

        // Category exist and deleted is null
        // Duplicate question check (same category + same question text)
        //  Options unique A != B != C != D
        // currectAns belong to option
        //  Image upload (if exists)

        Category category = categoryRepo.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Category not found or deleted"));

        boolean exists = questionRepository.existsByQuestionContentIgnoreCaseAndCategoryIdAndDeletedAtIsNull(request.getQuestionContent(), category.getId());

        if (exists) {
            throw new DuplicateResourceException("A similar question already exists in this category");
        }

        if (!QuestionHelper.isOptionsUnique(request.getOptionA(), request.getOptionB(), request.getOptionC(), request.getOptionD())) {

            throw new BadRequestException("Options must be unique. Duplicate options found.");
        }

        Question question = questionMapper.toEntity(request);
        question.setCategory(category);

        if (image != null && !image.isEmpty()) {

            String imageUrl = fileStorageService.upload(image);
            question.setImageUrl(imageUrl);
        }
        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

}
