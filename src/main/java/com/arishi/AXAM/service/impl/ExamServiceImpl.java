package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;

import com.arishi.AXAM.dto.responce.ExamStartResponse;
import com.arishi.AXAM.enums.BluePrintStatus;
import com.arishi.AXAM.enums.ExamAttemptStatus;
import com.arishi.AXAM.enums.ExamSchedulerStatus;
import com.arishi.AXAM.enums.QuestionsStatus;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamMapper;
import com.arishi.AXAM.model.*;

import com.arishi.AXAM.repo.*;

import com.arishi.AXAM.service.ExamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final BluePrintRepository bluePrintRepository;
    private final ExamMapper examMapper;

    private final ExamSchedulerRepository examSchedulerRepository;
    private final BluePrintDeteilRepository bluePrintDeteilRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository usersRepository;


    @Override
    public ExamResponse createExam(ExamRequest request) {

        boolean exists = examRepository.existsByTitleIgnoreCaseAndDeletedAtIsNull(request.getTitle());

        if (exists) {
            throw new DuplicateResourceException("Exam already exists: " + request.getTitle());
        }

        BluePrint bluePrint = bluePrintRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getBlueprintTitle()).orElseThrow(() -> new ResourceNotFoundException("Blueprint not found: " + request.getBlueprintTitle()));

        Exam exam = examMapper.toEntity(request);

        exam.setBluePrint(bluePrint);

        Exam savedExam = examRepository.save(exam);

        return examMapper.toResponse(savedExam);
    }


    @Override
    public List<ExamResponse> getAllExams() {

        List<Exam> exams = examRepository.findByDeletedAtIsNull();

        List<ExamResponse> responses = new ArrayList<>();

        for (Exam exam : exams) {
            responses.add(examMapper.toResponse(exam));
        }

        return responses;
    }


    @Override
    public ExamResponse getExamByTitle(String title) {

        Exam exam = examRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(title).orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + title));

        return examMapper.toResponse(exam);
    }


}