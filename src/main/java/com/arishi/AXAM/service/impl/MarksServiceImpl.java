package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.request.MarksRequest;
import com.arishi.AXAM.dto.responce.MarksResponse;
import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.exception.ExamNotFoundException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.model.Exam;
import com.arishi.AXAM.model.Marks;
import com.arishi.AXAM.repo.ExamRepository;
import com.arishi.AXAM.repo.MarksRepository;
import com.arishi.AXAM.service.MarksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarksServiceImpl implements MarksService {

    private final MarksRepository marksRepository;
    private final ExamRepository examRepository;

    @Override
    @Transactional
    public MarksResponse createMarks(MarksRequest request) {

        Exam exam = examRepository.findById(request.getExamId()).orElseThrow(() -> new ExamNotFoundException("Exam not found with id: " + request.getExamId()));

        Marks marks = marksRepository.findByExamIdAndDifficultyLevel(request.getExamId(), request.getDifficultyLevel()).orElse(new Marks());

        marks.setExam(exam);
        marks.setDifficultyLevel(request.getDifficultyLevel());
        marks.setMarks(request.getMarks());

        marks = marksRepository.save(marks);

        return mapToResponse(marks);
    }

    @Override
    @Transactional(readOnly = true)
    public MarksResponse getMarks(Long examId, DifficultyLevel difficultyLevel) {
        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found with id: " + examId);
        }
        Marks marks = marksRepository.findByExamIdAndDifficultyLevel(examId, difficultyLevel).orElseThrow(() -> new ResourceNotFoundException("Marks configuration not found for given exam and difficulty"));
        return mapToResponse(marks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarksResponse> getAllMarksForExam(Long examId) {

        if (!examRepository.existsById(examId)) {
            throw new ExamNotFoundException("Exam not found with id: " + examId);
        }
        return marksRepository.findByExamId(examId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMarks(Long marksId) {
        Marks marks = marksRepository.findById(marksId).orElseThrow(() -> new ResourceNotFoundException("Marks configuration not found"));
        marksRepository.delete(marks);
    }

    private MarksResponse mapToResponse(Marks marks) {
        return MarksResponse.builder().id(marks.getId()).examId(marks.getExam().getId()).difficultyLevel(marks.getDifficultyLevel()).marks(marks.getMarks()).build();
    }
}