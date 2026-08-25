package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;
import com.arishi.AXAM.enums.ExamStatus;
import com.arishi.AXAM.exception.BadRequestException;
import com.arishi.AXAM.exception.DuplicateResourceException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamMapper;
import com.arishi.AXAM.model.BluePrint;
import com.arishi.AXAM.model.Exam;
import com.arishi.AXAM.repo.BluePrintRepository;
import com.arishi.AXAM.repo.ExamAttemptRepository;
import com.arishi.AXAM.repo.ExamRepository;
import com.arishi.AXAM.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final BluePrintRepository bluePrintRepository;
    private final ExamMapper examMapper;
    private final ExamAttemptRepository examAttemptRepository;

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


    @Override
    public ExamResponse updateStatus(Long id, ExamStatus newStatus) {

        Exam exam = examRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));

        ExamStatus currentStatus = exam.getStatus();

        // DRAFT -> ACTIVE or INACTIVE
        if (currentStatus == ExamStatus.DRAFT) {
            if (newStatus != ExamStatus.ACTIVE && newStatus != ExamStatus.INACTIVE) {
                throw new BadRequestException("DRAFT exam can only be changed to ACTIVE or INACTIVE");
            }
        }

        // ACTIVE -> INACTIVE only
        if (currentStatus == ExamStatus.ACTIVE) {
            if (newStatus != ExamStatus.INACTIVE) {
                throw new BadRequestException("ACTIVE exam can only be changed to INACTIVE");
            }
        }

        // INACTIVE -> ACTIVE only (re-enable)
        if (currentStatus == ExamStatus.INACTIVE) {
            if (newStatus != ExamStatus.ACTIVE) {
                throw new BadRequestException("INACTIVE exam can only be changed to ACTIVE");
            }
        }

        exam.setStatus(newStatus);

        Exam savedExam = examRepository.save(exam);

        return examMapper.toResponse(savedExam);
    }

    @Override
    public ExamResponse updateExam(Long id, ExamRequest request) {

        Exam exam = examRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));


        if (examAttemptRepository.existsByExamId(id)) {
            throw new BadRequestException("Cannot edit an exam that already has candidate attempts");
        }

        if (!exam.getTitle().equalsIgnoreCase(request.getTitle())) {
            boolean titleTaken = examRepository.existsByTitleIgnoreCaseAndDeletedAtIsNull(request.getTitle());
            if (titleTaken) {
                throw new DuplicateResourceException("Exam already exists: " + request.getTitle());
            }
        }

        if (!exam.getBluePrint().getTitle().equalsIgnoreCase(request.getBlueprintTitle())) {
            BluePrint newBluePrint = bluePrintRepository.findByTitleIgnoreCaseAndDeletedAtIsNull(request.getBlueprintTitle()).orElseThrow(() -> new ResourceNotFoundException("Blueprint not found: " + request.getBlueprintTitle()));
            exam.setBluePrint(newBluePrint);
        }

        examMapper.updateEntity(exam, request);

        Exam savedExam = examRepository.save(exam);

        return examMapper.toResponse(savedExam);
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = examRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));

        if (examAttemptRepository.existsByExamId(id)) {
            throw new BadRequestException("Cannot delete an exam that already has candidate attempts");
        }

        exam.setDeletedAt(Instant.now());

        examRepository.save(exam);
    }
}