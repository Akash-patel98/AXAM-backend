package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.responce.AvailableExamResponse;
import com.arishi.AXAM.dto.responce.CandidateExamResponse;
import com.arishi.AXAM.dto.responce.ExamResultResponse;

import java.util.List;

public interface CandidateExamService {

    List<CandidateExamResponse> getMyExams();

    ExamResultResponse getExamResult(Long attemptId);

    List<AvailableExamResponse> getAvailableExams();
}