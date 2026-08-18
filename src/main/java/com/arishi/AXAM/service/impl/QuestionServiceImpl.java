package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.request.filter.QuestionFilterRequest;
import com.arishi.AXAM.dto.responce.PageResponse;
import com.arishi.AXAM.dto.responce.QuestionCsvError;
import com.arishi.AXAM.dto.responce.QuestionCsvUploadResponse;
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
import com.arishi.AXAM.service.FileStorageService;
import com.arishi.AXAM.service.QuestionService;
import com.arishi.AXAM.specification.QuestionSpecification;
import com.arishi.AXAM.util.QuestionHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request, MultipartFile image) {

        Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Category not found or deleted"));

        //dubliate  checks
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

        Question saved = questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    @Override
    public PageResponse<QuestionResponse> searchQuestions(QuestionFilterRequest request) {
        Specification<Question> specification = Specification.where(QuestionSpecification.isNotDeleted()).and(QuestionSpecification.hasCategory(request.getCategory())).and(QuestionSpecification.hasDifficultyLevel(request.getDifficultyLevel()));

        Sort sort = request.getSortDir().equalsIgnoreCase("asc") ? Sort.by(request.getSortBy()).ascending() : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Question> questions = questionRepository.findAll(specification, pageable);

        Page<QuestionResponse> mappedPage = questions.map(questionMapper::toResponse);
        return new PageResponse<>(mappedPage.getContent(), mappedPage.getNumber(), mappedPage.getSize(), mappedPage.getTotalElements(), mappedPage.getTotalPages(), mappedPage.isFirst(), mappedPage.isLast());
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long id, QuestionRequest request, MultipartFile image) {
        // Find existing question (not deleted)
        Question question = questionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));

        // Validate category
        Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Category not found or deleted"));

        // Duplicate check: if question content changed, ensure no duplicate in same category
        if (!question.getQuestionContent().equalsIgnoreCase(request.getQuestionContent())) {
            boolean exists = questionRepository.existsByQuestionContentIgnoreCaseAndCategoryIdAndDeletedAtIsNull(request.getQuestionContent(), category.getId());
            if (exists) {
                throw new DuplicateResourceException("A question with this content already exists in the category");
            }
        }

        if (!QuestionHelper.isOptionsUnique(request.getOptionA(), request.getOptionB(), request.getOptionC(), request.getOptionD())) {
            throw new BadRequestException("Options must be unique. Duplicate options found.");
        }

        // Update fields
        question.setQuestionContent(request.getQuestionContent());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setCategory(category);

        // Handle image update
        if (image != null && !image.isEmpty()) {
            String newImageUrl = fileStorageService.upload(image);
            question.setImageUrl(newImageUrl);
        }

        Question updated = questionRepository.save(question);
        return questionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteQuestionById(Long id) {
        Question question = questionRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
        question.setDeletedAt(Instant.now());
        questionRepository.save(question);
    }

    @Override
    public Resource downloadQuestionsCsv() {
        List<Question> questions = questionRepository.findAllByDeletedAtIsNull();
        StringWriter writer = new StringWriter();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("category", "questionContent", "optionA", "optionB", "optionC", "optionD", "correctAnswer", "difficultyLevel").build())) {

            for (Question question : questions) {
                csvPrinter.printRecord(question.getCategory().getTitle(), question.getQuestionContent(), question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(), question.getCorrectAnswer(), question.getDifficultyLevel());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create questions CSV", e);
        }

        return new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Resource downloadSampleCsv() {
        StringWriter writer = new StringWriter();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("category", "questionContent", "optionA", "optionB", "optionC", "optionD", "correctAnswer", "difficultyLevel").build())) {

        } catch (IOException e) {
            throw new RuntimeException("Failed to create sample CSV", e);
        }
        return new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
    }


    @Override
    @Transactional
    public QuestionCsvUploadResponse uploadQuestionsCsv(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("CSV file is required");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new BadRequestException("Only CSV files are allowed");
        }

        List<Question> validQuestions = new ArrayList<>();
        List<QuestionCsvError> errors = new ArrayList<>();

        Map<String, Category> categoryCache = new HashMap<>();
        Set<String> csvQuestionKeys = new HashSet<>();

        int totalRows = 0;

        List<String> requiredHeaders = List.of("category", "questionContent", "optionA", "optionB", "optionC", "optionD", "correctAnswer", "difficultyLevel");

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreEmptyLines(true).setTrim(true).build().parse(reader)) {

            // validate csv header
            Map<String, Integer> headerMap = parser.getHeaderMap();

            for (String requiredHeader : requiredHeaders) {

                if (!headerMap.containsKey(requiredHeader)) {

                    throw new BadRequestException("Missing required CSV column: " + requiredHeader);
                }
            }

            //  validate each row
            for (CSVRecord record : parser) {

                totalRows++;

                long rowNumber = record.getRecordNumber();

                // validate  number of columns
                if (record.size() != requiredHeaders.size()) {

                    errors.add(new QuestionCsvError(rowNumber, "", "Invalid number of columns. Expected " + requiredHeaders.size() + " but found " + record.size()));

                    continue;
                }


                // Read values
                String categoryTitle = getValue(record, "category");
                String questionContent = getValue(record, "questionContent");
                String optionA = getValue(record, "optionA");
                String optionB = getValue(record, "optionB");
                String optionC = getValue(record, "optionC");
                String optionD = getValue(record, "optionD");
                String correctAnswer = getValue(record, "correctAnswer");
                String difficulty = getValue(record, "difficultyLevel");


                // Required field validatio
                String requiredFieldError = validateRequiredFields(categoryTitle, questionContent, optionA, optionB, optionC, optionD, correctAnswer, difficulty);

                if (requiredFieldError != null) {

                    errors.add(new QuestionCsvError(rowNumber, questionContent, requiredFieldError));

                    continue;
                }


                // Validate option uniqueness
                if (!QuestionHelper.isOptionsUnique(optionA, optionB, optionC, optionD)) {
                    errors.add(new QuestionCsvError(rowNumber, questionContent, "Options must be unique"));

                    continue;
                }

                // Validate correct answer
                if (!isCorrectAnswerValid(correctAnswer)) {
                    errors.add(new QuestionCsvError(rowNumber, questionContent, "Correct answer must be A, B, C or D"));

                    continue;
                }

                // 6. Validate difficulty

                DifficultyLevel difficultyLevel;

                try {

                    difficultyLevel = DifficultyLevel.valueOf(difficulty.toUpperCase(Locale.ROOT));

                } catch (IllegalArgumentException ex) {

                    errors.add(new QuestionCsvError(rowNumber, questionContent, "Invalid difficulty level: " + difficulty));

                    continue;
                }


                //Find category
                String normalizedCategory = normalize(categoryTitle);
                Category category = categoryCache.get(normalizedCategory);

                if (category == null) {

                    Optional<Category> categoryOptional = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(categoryTitle);

                    if (categoryOptional.isEmpty()) {

                        errors.add(new QuestionCsvError(rowNumber, questionContent, "Category not found: " + categoryTitle));

                        continue;
                    }

                    category = categoryOptional.get();

                    categoryCache.put(normalizedCategory, category);
                }


                // Duplicate inside CSV
                String questionKey = category.getId() + "|" + normalize(questionContent);

                if (!csvQuestionKeys.add(questionKey)) {

                    errors.add(new QuestionCsvError(rowNumber, questionContent, "Duplicate question found inside CSV"));

                    continue;
                }


                // Duplicate in database
                boolean alreadyExists = questionRepository.existsByQuestionContentIgnoreCaseAndCategoryIdAndDeletedAtIsNull(questionContent, category.getId());

                if (alreadyExists) {

                    errors.add(new QuestionCsvError(rowNumber, questionContent, "Question already exists"));

                    continue;
                }


                // Create Question
                Question question = new Question();

                question.setCategory(category);
                question.setQuestionContent(questionContent);
                question.setOptionA(optionA);
                question.setOptionB(optionB);
                question.setOptionC(optionC);
                question.setOptionD(optionD);
                question.setCorrectAnswer(correctAnswer.toUpperCase(Locale.ROOT));
                question.setDifficultyLevel(difficultyLevel);

                validQuestions.add(question);
            }

            // Save valid questions
            if (!validQuestions.isEmpty()) questionRepository.saveAll(validQuestions);


        } catch (IOException ex) {

            throw new BadRequestException("Failed to read CSV file");
        }

        return new QuestionCsvUploadResponse(totalRows, validQuestions.size(), errors.size(), errors);
    }


    private String validateRequiredFields(String category, String questionContent, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String difficulty) {

        if (category.isBlank()) return "Category is required";

        if (questionContent.isBlank()) return "Question content is required";

        if (optionA.isBlank()) return "Option A is required";

        if (optionB.isBlank()) return "Option B is required";

        if (optionC.isBlank()) return "Option C is required";

        if (optionD.isBlank()) return "Option D is required";

        if (correctAnswer.isBlank()) return "Correct answer is required";

        if (difficulty.isBlank()) return "Difficulty level is required";

        return null;
    }

    private String getValue(CSVRecord record, String column) {
        String value = record.get(column);

        return value == null ? "" : value.trim();
    }

    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private boolean isCorrectAnswerValid(String correctAnswer) {
        return Set.of("A", "B", "C", "D").contains(correctAnswer.toUpperCase(Locale.ROOT));
    }
}
