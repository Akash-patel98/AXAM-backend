package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamMapper;
import com.arishi.AXAM.model.BluePrint;
import com.arishi.AXAM.model.Exam;
import com.arishi.AXAM.repo.BluePrintRepository;
import com.arishi.AXAM.repo.ExamRepository;
import com.arishi.AXAM.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final BluePrintRepository bluePrintRepository;
    private final ExamMapper examMapper;

    @Override
    public ExamResponse createExam(ExamRequest request) {

        // Check duplicate exam
        boolean exists = examRepository.existsByTitleIgnoreCaseAndDeletedAtIsNull(request.getTitle());

        if (exists) throw new DuplicateResourceException("Exam already exists: " + request.getTitle());

        // Find blueprint
        BluePrint bluePrint = bluePrintRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getBlueprintTitle()).orElseThrow(() -> new ResourceNotFoundException("Blueprint not found: " + request.getBlueprintTitle()));

        // Create exam
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