package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.BlueprintDetailRequest;
import com.arishi.AXAM.dto.request.BlueprintRequest;
import com.arishi.AXAM.dto.responce.BluePrintResponse;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.BluePrintMapper;
import com.arishi.AXAM.model.BluePrint;
import com.arishi.AXAM.model.BluePrintDeteil;
import com.arishi.AXAM.model.Category;
import com.arishi.AXAM.repo.BluePrintDeteilRepository;
import com.arishi.AXAM.repo.BluePrintRepository;
import com.arishi.AXAM.repo.CategoryRepository;
import com.arishi.AXAM.repo.QuestionRepository;
import com.arishi.AXAM.service.BluePrintService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class BluePrintServiceImpl implements BluePrintService {

    private final BluePrintRepository bluePrintRepository;
    private final BluePrintDeteilRepository bluePrintDeteilRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final BluePrintMapper bluePrintMapper;

    @Transactional
    @Override
    public BluePrintResponse createBlueprint(BlueprintRequest request) {

        // check Duplicate Blueprint
        boolean exists = bluePrintRepository.existsByTitleIgnoreCaseAndDeletedAtIsNull(request.getTitle());

        if (exists) {
            throw new DuplicateResourceException("Blueprint already exists: " + request.getTitle());
        }

        List<BluePrintDeteil> details = new ArrayList<>();

        for (BlueprintDetailRequest detailRequest : request.getDetails()) {

            // check category exist or not
            Category category = categoryRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(detailRequest.getCategoryTitle()).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + detailRequest.getCategoryTitle()));

            // check enough quction avalable or not
            long available = questionRepository.countByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(category.getId(), detailRequest.getDifficultyLevel());

            if (available < detailRequest.getQuestionCount()) {
                throw new BadRequestException("Not enough questions available for category upload " + category.getTitle() + "' and difficulty '" + detailRequest.getDifficultyLevel() + "'. Required: " + detailRequest.getQuestionCount() + ", Available: " + available);
            }

            BluePrintDeteil detail = bluePrintMapper.toDetailEntity(detailRequest, category);

            details.add(detail);
        }

        BluePrint bluePrint = bluePrintMapper.toEntity(request);

        BluePrint savedBlueprint = bluePrintRepository.save(bluePrint);

        for (BluePrintDeteil detail : details) detail.setBluePrint(savedBlueprint);

        bluePrintDeteilRepository.saveAll(details);

        return bluePrintMapper.toResponse(savedBlueprint, details);
    }


    // Get all blueprint
    @Override
    public List<BluePrintResponse> getAllBlueprints() {

        List<BluePrint> blueprints = bluePrintRepository.findByDeletedAtIsNull();

        List<BluePrintResponse> responses = new ArrayList<>();

        for (BluePrint bluePrint : blueprints) {

            List<BluePrintDeteil> details = bluePrintDeteilRepository.findByBluePrintIdAndDeletedAtIsNull(bluePrint.getId());

            responses.add(bluePrintMapper.toResponse(bluePrint, details));
        }

        return responses;
    }


    @Override
    public BluePrintResponse getBlueprintByTitle(String title) {

        BluePrint bluePrint = bluePrintRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(title).orElseThrow(() -> new ResourceNotFoundException("Blueprint not found: " + title));

        List<BluePrintDeteil> details = bluePrintDeteilRepository.findByBluePrintIdAndDeletedAtIsNull(bluePrint.getId());

        return bluePrintMapper.toResponse(bluePrint, details);
    }
}