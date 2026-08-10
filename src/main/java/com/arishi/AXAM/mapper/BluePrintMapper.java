package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.request.BlueprintDetailRequest;
import com.arishi.AXAM.dto.request.BlueprintRequest;
import com.arishi.AXAM.dto.responce.BluePrintDetailResponse;
import com.arishi.AXAM.dto.responce.BluePrintResponse;
import com.arishi.AXAM.enums.BluePrintStatus;
import com.arishi.AXAM.model.BluePrint;
import com.arishi.AXAM.model.BluePrintDeteil;
import com.arishi.AXAM.model.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BluePrintMapper {

    public BluePrint toEntity(BlueprintRequest request) {

        return BluePrint.builder().title(request.getTitle()).description(request.getDescription()).bluePrintStatus(BluePrintStatus.DRAFT).build();
    }

    public BluePrintDeteil toDetailEntity(BlueprintDetailRequest request, Category category) {

        return BluePrintDeteil.builder().category(category).difficultyLevel(request.getDifficultyLevel()).questionCount(request.getQuestionCount()).build();
    }

    public BluePrintResponse toResponse(BluePrint bluePrint, List<BluePrintDeteil> details) {

        List<BluePrintDetailResponse> detailResponses = new ArrayList<>();

        for (BluePrintDeteil detail : details) {

            BluePrintDetailResponse response = BluePrintDetailResponse.builder().id(detail.getId()).categoryId(detail.getCategory().getId()).categoryTitle(detail.getCategory().getTitle()).difficultyLevel(detail.getDifficultyLevel()).questionCount(detail.getQuestionCount()).build();

            detailResponses.add(response);
        }

        return BluePrintResponse.builder().id(bluePrint.getId()).title(bluePrint.getTitle()).description(bluePrint.getDescription()).status(bluePrint.getBluePrintStatus()).details(detailResponses).build();
    }
}