package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.ExamRequest;
import com.arishi.AXAM.dto.responce.ExamResponse;
import com.arishi.AXAM.enums.ExamStatus;

import java.util.List;

public interface ExamService {

    ExamResponse createExam(ExamRequest request);

    List<ExamResponse> getAllExams();

    ExamResponse getExamByTitle(String title);


    ExamResponse updateStatus(Long id, ExamStatus newStatus);

    ExamResponse updateExam(Long id, ExamRequest request);

    void deleteExam(Long id);
}
