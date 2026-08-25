package com.arishi.AXAM.service.impl;

import com.arishi.AXAM.dto.responce.AvailableExamResponse;
import com.arishi.AXAM.dto.responce.CandidateExamResponse;
import com.arishi.AXAM.dto.responce.ExamResultResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.enums.BluePrintStatus;
import com.arishi.AXAM.enums.ExamSchedulerStatus;
import com.arishi.AXAM.enums.ExamStatus;
import com.arishi.AXAM.exception.ForbiddenException;
import com.arishi.AXAM.exception.ResourceNotFoundException;
import com.arishi.AXAM.mapper.ExamAttemptMapper;
import com.arishi.AXAM.model.AttemptQuestion;
import com.arishi.AXAM.model.Exam;
import com.arishi.AXAM.model.ExamAttempt;
import com.arishi.AXAM.model.ExamScheduler;
import com.arishi.AXAM.repo.AttemptQuestionRepository;
import com.arishi.AXAM.repo.ExamAttemptRepository;
import com.arishi.AXAM.repo.ExamRepository;
import com.arishi.AXAM.repo.ExamSchedulerRepository;
import com.arishi.AXAM.repo.UserRepository;
import com.arishi.AXAM.security.CustomUserDetails;
import com.arishi.AXAM.service.CandidateExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateExamServiceImpl implements CandidateExamService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final ExamSchedulerRepository examSchedulerRepository;
    private final UserRepository userRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final ExamAttemptMapper examAttemptMapper;

    @Override
    public List<CandidateExamResponse> getMyExams() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ExamAttempt> attempts = examAttemptRepository.findByUserEmailAndDeletedAtIsNull(email);

        return attempts.stream().map(this::mapToResponse).toList();
    }

    private CandidateExamResponse mapToResponse(ExamAttempt attempt) {

        return CandidateExamResponse.builder().examId(attempt.getExam().getId()).attemptId(attempt.getId()).examTitle(attempt.getExam().getTitle()).startAt(attempt.getStartAt()).endAt(attempt.getEndAt()).totalQuestions(attempt.getTotalQuestions()).status(attempt.getStatus()).build();
    }

    @Override
    public ExamResultResponse getExamResult(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        Long currentUserId = getCurrentUserId();

        if (!attempt.getUser().getId().equals(currentUserId)) {

            throw new ResourceNotFoundException("Exam attempt not found");
        }

        String resultStatus = null;
        Float passingPercentage = attempt.getExam() != null ? attempt.getExam().getPassingPercentage() : null;

        if (passingPercentage != null && attempt.getPercentage() != null) {
            resultStatus = attempt.getPercentage() >= passingPercentage ? "PASSED" : "FAILED";
        }

        List<AttemptQuestion> attemptQuestions = attemptQuestionRepository.findByExamAttempt(attempt);

        List<SubmitExamResponse.QuestionResultDTO> questionResults = attemptQuestions.stream()
                .sorted((a, b) -> a.getDisplayOrder().compareTo(b.getDisplayOrder()))
                .map(examAttemptMapper::toQuestionResultDTO)
                .collect(Collectors.toList());

        return ExamResultResponse.builder().attemptId(attempt.getId())

                .examId(attempt.getExam().getId()).examName(attempt.getExam().getTitle())

                .attemptStatus(attempt.getStatus() != null ? attempt.getStatus().name() : null)

                .totalQuestions(attempt.getTotalQuestions()).attemptedQuestions(attempt.getAttemptedQuestions()).unattemptedQuestions(attempt.getUnattemptedQuestions())

                .correctAnswers(attempt.getCorrectAnswers()).incorrectAnswers(attempt.getIncorrectAnswers())

                .obtainedMarks(attempt.getObtainedMarks()).totalMarks(attempt.getTotalMarks())

                .percentage(attempt.getPercentage())

                .resultStatus(resultStatus)

                .questionResults(questionResults)

                .build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser().getId();
        }

        throw new ForbiddenException("Unable to resolve current user");
    }

    @Override
    public List<AvailableExamResponse> getAvailableExams() {

        Long currentUserId = getCurrentUserId();

        List<Exam> activeExams = examRepository.findByDeletedAtIsNull().stream()
                .filter(exam -> exam.getStatus() == ExamStatus.ACTIVE)
                .filter(exam -> exam.getBluePrint() != null
                        && exam.getBluePrint().getBluePrintStatus() == BluePrintStatus.ACTIVE)
                .toList();

        Instant now = Instant.now();
        List<AvailableExamResponse> result = new ArrayList<>();

        for (Exam exam : activeExams) {

            List<ExamScheduler> schedulers = examSchedulerRepository
                    .findByExam_IdAndStatusAndDeletedAtIsNull(exam.getId(), ExamSchedulerStatus.ACTIVE);

            for (ExamScheduler scheduler : schedulers) {

                boolean withinWindow = !now.isBefore(scheduler.getStartDate()) && now.isBefore(scheduler.getEndDate());

                if (!withinWindow) continue;

                int attemptsUsed = examAttemptRepository.countByUserIdAndSchedulerId(currentUserId, scheduler.getId());
                int attemptsRemaining = scheduler.getMaxAttempts() - attemptsUsed;

                if (attemptsRemaining <= 0) continue;

                result.add(AvailableExamResponse.builder()
                        .examId(exam.getId())
                        .title(exam.getTitle())
                        .description(exam.getDescription())
                        .instruction(exam.getInstruction())
                        .duration(exam.getDuration())
                        .passingPercentage(exam.getPassingPercentage())
                        .schedulerId(scheduler.getId())
                        .startDate(scheduler.getStartDate())
                        .endDate(scheduler.getEndDate())
                        .maxAttempts(scheduler.getMaxAttempts())
                        .attemptsUsed(attemptsUsed)
                        .attemptsRemaining(attemptsRemaining)
                        .build());
            }
        }

        return result;
    }
}