package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.AnswerRequest;
import com.arishi.AXAM.dto.request.StartAttemptRequest;
import com.arishi.AXAM.dto.responce.StartExamResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamAttemptService {

    StartExamResponse startAttempt(StartAttemptRequest request, Long userId);

    void submitAnswer(Long attemptId, String sessionId, AnswerRequest request, Long userId);

    SubmitExamResponse submitExam(Long attemptId, String sessionId, Long userId);

    ExamAttempt getAttemptDetails(Long attemptId);

    List<ExamAttempt> getUserAttempts(Long userId);

    Page<ExamAttempt> getUserAttemptsPaginated(Long userId, Pageable pageable);

    boolean hasActiveExam(Long userId);

    ExamAttempt getActiveExam(Long userId);

    void abandonExam(Long attemptId, String sessionId, Long userId);

    void calculateAndUpdateResults(ExamAttempt attempt);
}
