package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.request.ExamSchedulerRequest;
import com.arishi.AXAM.dto.responce.ExamSchedulerResponse;
import com.arishi.AXAM.model.ExamScheduler;
import org.springframework.stereotype.Component;

@Component
public class ExamSchedulerMapper {

    public ExamScheduler toEntity(ExamSchedulerRequest request) {

        return ExamScheduler.builder().startDate(request.getStartDate()).endDate(request.getEndDate()).title(request.getTitle()).description(request.getDescription()).maxAttempts(request.getMaxAttempts()).build();
    }

    public ExamSchedulerResponse toResponse(ExamScheduler scheduler) {

        return ExamSchedulerResponse.builder().id(scheduler.getId()).examId(scheduler.getExam().getId()).examTitle(scheduler.getExam().getTitle()).status(scheduler.getStatus()).startDate(scheduler.getStartDate()).endDate(scheduler.getEndDate()).title(scheduler.getTitle()).description(scheduler.getDescription()).maxAttempts(scheduler.getMaxAttempts()).build();
    }
}