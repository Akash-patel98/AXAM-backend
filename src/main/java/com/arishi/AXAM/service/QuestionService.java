package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.QuestionRequest;
import com.arishi.AXAM.dto.responce.QuestionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request, MultipartFile image);

}