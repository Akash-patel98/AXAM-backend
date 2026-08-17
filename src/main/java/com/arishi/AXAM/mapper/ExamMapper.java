package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;
import com.arishi.AXAM.model.Exam;
import org.springframework.stereotype.Component;

@Component
public class ExamMapper {

    public Exam toEntity(ExamRequest request) {

        return Exam.builder().title(request.getTitle()).description(request.getDescription()).instruction(request.getInstruction()).passingPercentage(request.getPassingPercentage()).duration(request.getDuration()).status(com.arishi.AXAM.enums.ExamStatus.DRAFT).build();
    }

    public ExamResponse toResponse(Exam exam) {

        return ExamResponse.builder().id(exam.getId()).title(exam.getTitle()).description(exam.getDescription()).instruction(exam.getInstruction()).passingPercentage(exam.getPassingPercentage()).duration(exam.getDuration()).status(exam.getStatus()).blueprintId(exam.getBluePrint().getId()).blueprintTitle(exam.getBluePrint().getTitle()).build();
    }
}