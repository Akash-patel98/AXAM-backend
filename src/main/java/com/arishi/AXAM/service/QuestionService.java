package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.request.filter.QuestionFilterRequest;
import com.arishi.AXAM.dto.responce.PageResponse;
import com.arishi.AXAM.dto.responce.QuestionCsvUploadResponse;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request, MultipartFile image);

    PageResponse<QuestionResponse> searchQuestions(QuestionFilterRequest request);

    QuestionResponse updateQuestion(Long id, QuestionRequest request, MultipartFile image);

    void deleteQuestionById(Long id);

    // CSV related methods (to support the controller)
    Resource downloadSampleCsv();

    QuestionCsvUploadResponse uploadQuestionsCsv(MultipartFile file);

    Resource downloadQuestionsCsv();
}
