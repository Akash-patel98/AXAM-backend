package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.responce.AdminAttemptDetailResponse;
import com.arishi.AXAM.dto.responce.AdminAttemptSummaryResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamAttemptMapper;
import com.arishi.AXAM.model.AttemptQuestion;
import com.arishi.AXAM.model.ExamAttempt;
import com.arishi.AXAM.repo.AttemptQuestionRepository;
import com.arishi.AXAM.repo.ExamAttemptRepository;
import com.arishi.AXAM.service.AdminAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAttemptServiceImpl implements AdminAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final ExamAttemptMapper examAttemptMapper;

    @Override
    public Page<AdminAttemptSummaryResponse> getAllAttempts(Pageable pageable) {

        Page<ExamAttempt> attempts = examAttemptRepository.findAllByDeletedAtIsNull(pageable);

        List<AdminAttemptSummaryResponse> summaries = new ArrayList<>();

        for (ExamAttempt attempt : attempts.getContent()) {
            summaries.add(toSummary(attempt));
        }

        return new PageImpl<>(summaries, pageable, attempts.getTotalElements());
    }

    @Override
    public Page<AdminAttemptSummaryResponse> getAttemptsByExam(Long examId, Pageable pageable) {

        Page<ExamAttempt> attempts = examAttemptRepository.findByExamIdAndDeletedAtIsNull(examId, pageable);

        List<AdminAttemptSummaryResponse> summaries = new ArrayList<>();

        for (ExamAttempt attempt : attempts.getContent()) {
            summaries.add(toSummary(attempt));
        }

        return new PageImpl<>(summaries, pageable, attempts.getTotalElements());
    }

    @Override
    public Page<AdminAttemptSummaryResponse> getAttemptsByCandidate(Long candidateId, Pageable pageable) {

        Page<ExamAttempt> attempts = examAttemptRepository.findByUserId(candidateId, pageable);

        List<AdminAttemptSummaryResponse> summaries = new ArrayList<>();

        for (ExamAttempt attempt : attempts.getContent()) {
            summaries.add(toSummary(attempt));
        }

        return new PageImpl<>(summaries, pageable, attempts.getTotalElements());
    }

    @Override
    public AdminAttemptDetailResponse getAttemptDetail(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + attemptId));

        List<AttemptQuestion> attemptQuestions = attemptQuestionRepository.findByExamAttempt(attempt);

        List<SubmitExamResponse.QuestionResultDTO> questionResults = new ArrayList<>();

        attemptQuestions.sort(new Comparator<AttemptQuestion>() {
            @Override
            public int compare(AttemptQuestion a, AttemptQuestion b) {
                return a.getDisplayOrder().compareTo(b.getDisplayOrder());
            }
        });

        for (AttemptQuestion attemptQuestion : attemptQuestions) {

            SubmitExamResponse.QuestionResultDTO result = examAttemptMapper.toQuestionResultDTO(attemptQuestion);

            questionResults.add(result);
        }

        return AdminAttemptDetailResponse.builder().attemptId(attempt.getId()).examId(attempt.getExam().getId()).examTitle(attempt.getExam().getTitle()).candidateId(attempt.getUser().getId()).candidateName(attempt.getUser().getFirstName() + " " + attempt.getUser().getLastName()).candidateEmail(attempt.getUser().getEmail()).status(attempt.getStatus()).startAt(attempt.getStartAt()).endAt(attempt.getEndAt()).totalQuestions(attempt.getTotalQuestions()).attemptedQuestions(attempt.getAttemptedQuestions()).unattemptedQuestions(attempt.getUnattemptedQuestions()).correctAnswers(attempt.getCorrectAnswers()).incorrectAnswers(attempt.getIncorrectAnswers()).obtainedMarks(attempt.getObtainedMarks()).totalMarks(attempt.getTotalMarks()).percentage(attempt.getPercentage()).questionResults(questionResults).build();
    }

    private AdminAttemptSummaryResponse toSummary(ExamAttempt attempt) {

        return AdminAttemptSummaryResponse.builder().attemptId(attempt.getId()).examId(attempt.getExam().getId()).examTitle(attempt.getExam().getTitle()).candidateId(attempt.getUser().getId()).candidateName(attempt.getUser().getFirstName() + " " + attempt.getUser().getLastName()).candidateEmail(attempt.getUser().getEmail()).status(attempt.getStatus()).startAt(attempt.getStartAt()).endAt(attempt.getEndAt()).totalQuestions(attempt.getTotalQuestions()).correctAnswers(attempt.getCorrectAnswers()).obtainedMarks(attempt.getObtainedMarks()).totalMarks(attempt.getTotalMarks()).percentage(attempt.getPercentage()).build();
    }
}