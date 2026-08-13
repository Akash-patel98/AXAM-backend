package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.request.ExamSchedulerRequest;
import com.arishi.AXAM.dto.responce.ExamSchedulerResponse;
import com.arishi.AXAM.model.ExamScheduler;
import org.springframework.stereotype.Component;

@Component
public class ExamSchedulerMapper {

    public ExamScheduler toEntity(ExamSchedulerRequest request) {

        return ExamScheduler.builder().startTime(request.getStartTime()).endTime(request.getEndTime()).build();
    }

    public ExamSchedulerResponse toResponse(ExamScheduler scheduler) {

        return ExamSchedulerResponse.builder().id(scheduler.getId()).examId(scheduler.getExam().getId()).examTitle(scheduler.getExam().getTitle()).status(scheduler.getStatus()).startTime(scheduler.getStartTime()).endTime(scheduler.getEndTime()).build();
    }
}