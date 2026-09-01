package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.responce.AdminAttemptDetailResponse;
import com.arishi.AXAM.dto.responce.AdminAttemptSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminAttemptService {

    Page<AdminAttemptSummaryResponse> getAllAttempts(Pageable pageable);

    Page<AdminAttemptSummaryResponse> getAttemptsByExam(Long examId, Pageable pageable);

    Page<AdminAttemptSummaryResponse> getAttemptsByCandidate(Long candidateId, Pageable pageable);

    AdminAttemptDetailResponse getAttemptDetail(Long attemptId);
}