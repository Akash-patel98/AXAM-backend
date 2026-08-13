package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.ExamSchedulerRequest;
import com.arishi.AXAM.dto.responce.ExamSchedulerResponse;
import com.arishi.AXAM.enums.ExamSchedulerStatus;
import com.arishi.AXAM.model.ExamScheduler;

import java.util.List;
import java.util.Optional;

public interface ExamSchedulerService {
    public ExamSchedulerResponse createScheduler(ExamSchedulerRequest request);

    public ExamSchedulerResponse updateStatus(Long id, ExamSchedulerStatus newStatus);

    List<ExamSchedulerResponse> getAllSchedulers();


    ExamSchedulerResponse getSchedulerById(Long examSchedulerId);
}
