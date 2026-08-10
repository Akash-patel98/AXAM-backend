package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;

import java.util.List;

public interface ExamService {

    ExamResponse createExam(ExamRequest request);

    List<ExamResponse> getAllExams();

    ExamResponse getExamByTitle(String title);
}