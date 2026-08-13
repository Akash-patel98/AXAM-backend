package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.ExamSchedulerRequest;
import com.arishi.AXAM.dto.responce.ExamSchedulerResponse;
import com.arishi.AXAM.enums.ExamSchedulerStatus;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamSchedulerMapper;
import com.arishi.AXAM.model.Exam;
import com.arishi.AXAM.model.ExamScheduler;
import com.arishi.AXAM.repo.ExamRepository;
import com.arishi.AXAM.repo.ExamSchedulerRepository;
import com.arishi.AXAM.service.ExamSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamSchedulerServiceImpl implements ExamSchedulerService {

    private final ExamSchedulerRepository examSchedulerRepository;
    private final ExamRepository examRepository;
    private final ExamSchedulerMapper examSchedulerMapper;

    @Override
    public ExamSchedulerResponse createScheduler(ExamSchedulerRequest request) {

        // Check exam available
        Exam exam = examRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getExamTitle()).orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + request.getExamTitle()));

        // check same exam schedul for given time
        boolean exists = examSchedulerRepository.existsByExamIdAndStartTimeAndEndTime(exam.getId(), request.getStartTime(), request.getEndTime());

        if (exists) throw new DuplicateResourceException("This exam is already scheduled for the given time");

        // Check start time and end time
        if (!request.getStartTime().isBefore(request.getEndTime()))
            throw new BadRequestException("Start time must be before end time");

        // Create scheduler
        ExamScheduler scheduler = examSchedulerMapper.toEntity(request);

        scheduler.setExam(exam);
        scheduler.setStatus(ExamSchedulerStatus.DRAFT);

        ExamScheduler savedScheduler = examSchedulerRepository.save(scheduler);

        return examSchedulerMapper.toResponse(savedScheduler);
    }


    @Override
    public List<ExamSchedulerResponse> getAllSchedulers() {

        List<ExamScheduler> schedulers = examSchedulerRepository.findAllByDeletedAtIsNullOrderByStartTimeAsc();

        List<ExamSchedulerResponse> responses = new ArrayList<>();

        for (ExamScheduler scheduler : schedulers) {

            ExamSchedulerResponse response = examSchedulerMapper.toResponse(scheduler);

            responses.add(response);
        }

        return responses;
    }


    @Override
    public ExamSchedulerResponse getSchedulerById(Long examSchedulerId) {

        ExamScheduler scheduler = examSchedulerRepository.findByIdAndDeletedAtIsNull(examSchedulerId).orElseThrow(() -> new ResourceNotFoundException("Exam scheduler not found: " + examSchedulerId));

        return examSchedulerMapper.toResponse(scheduler);
    }


    @Override
    public ExamSchedulerResponse updateStatus(Long id, ExamSchedulerStatus newStatus) {

        ExamScheduler scheduler = examSchedulerRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Exam scheduler not found: " + id));

        ExamSchedulerStatus currentStatus = scheduler.getStatus();

        // Completeed status cannot change
        if (currentStatus == ExamSchedulerStatus.COMPLETED) {
            throw new BadRequestException("Completed scheduler status cannot be changed");
        }


        // canceled status cannot change
        if (currentStatus == ExamSchedulerStatus.CANCELED) {
            throw new BadRequestException("Canceled scheduler status cannot be changed");
        }

        // Draft status can be change active or canceled
        if (currentStatus == ExamSchedulerStatus.DRAFT) {

            if (newStatus != ExamSchedulerStatus.ACTIVE && newStatus != ExamSchedulerStatus.CANCELED) {

                throw new BadRequestException("DRAFT scheduler can only be changed");
            }
        }
        // change active status to canceled
        if (currentStatus == ExamSchedulerStatus.ACTIVE) {

            if (newStatus != ExamSchedulerStatus.CANCELED) {

                throw new BadRequestException("ACTIVE scheduler can only be changed to CANCELED");
            }
        }

        scheduler.setStatus(newStatus);

        ExamScheduler savedScheduler = examSchedulerRepository.save(scheduler);

        return examSchedulerMapper.toResponse(savedScheduler);
    }
}