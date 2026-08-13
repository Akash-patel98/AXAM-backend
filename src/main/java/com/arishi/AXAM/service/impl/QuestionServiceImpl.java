package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.QuestionMapper;
import com.arishi.AXAM.model.Category;
import com.arishi.AXAM.model.Question;
import com.arishi.AXAM.repo.CategoryRepository;
import com.arishi.AXAM.repo.QuestionRepository;
import com.arishi.AXAM.service.QuestionService;
import com.arishi.AXAM.service.FileStorageService;
import com.arishi.AXAM.util.QuestionHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {


    private final CategoryRepository categoryRepository;
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

        Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Category not found or deleted"));

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

    @Override
    public List<QuestionResponse> getAllQuestions() {

        List<Question> questions = questionRepository.findAllByDeletedAtIsNull();

        List<QuestionResponse> responses = new ArrayList<>();

        for (Question question : questions) {
            QuestionResponse response = questionMapper.toResponse(question);

            responses.add(response);
        }

        return responses;
    }

    @Override
    public List<QuestionResponse> getQuestionsByCategory(String categoryTitle) {

        //check category exist
        Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(categoryTitle).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryTitle));

        List<Question> questions = questionRepository.findAllByCategoryTitleIgnoreCaseAndDeletedAtIsNull(categoryTitle);

        List<QuestionResponse> responses = new ArrayList<>();

        for (Question question : questions) {
            QuestionResponse response = questionMapper.toResponse(question);

            responses.add(response);
        }

        return responses;
    }


    // filter  questions by category and defficulty  level
    @Override
    public List<QuestionResponse> getQuestionsByCategoryAndDifficulty(String categoryTitle, DifficultyLevel difficultyLevel) {

        Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(categoryTitle).orElseThrow(() -> new ResourceNotFoundException("Category not found:" + categoryTitle));

        List<Question> questions = questionRepository.findAllByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(category.getId(), difficultyLevel);

        List<QuestionResponse> responses = new ArrayList<>();

        for (Question question : questions) {
            QuestionResponse response = questionMapper.toResponse(question);

            responses.add(response);
        }

        return responses;
    }


    @Override
    @Transactional
    public void deleteQuestionById(Long id) {

        Question question = questionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));

        question.setDeletedAt(Instant.now());

        questionRepository.save(question);
    }

    @Override
    public QuestionResponse updateQuestion(Long id, QuestionRequest request, MultipartFile image) {
        return null;
    }


}
