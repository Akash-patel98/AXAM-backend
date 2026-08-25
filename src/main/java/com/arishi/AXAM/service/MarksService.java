package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.MarksRequest;
import com.arishi.AXAM.dto.responce.MarksResponse;
import com.arishi.AXAM.enums.DifficultyLevel;

import java.util.List;

public interface MarksService {

    MarksResponse createMarks(MarksRequest request);

    MarksResponse getMarks(Long examId, DifficultyLevel difficultyLevel);

    List<MarksResponse> getAllMarksForExam(Long examId);

    void deleteMarks(Long marksId);
}