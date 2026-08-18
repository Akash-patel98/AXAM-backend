package com.arishi.AXAM.mapper;

import com.arishi.AXAM.dto.responce.StartExamResponse;
import com.arishi.AXAM.dto.responce.SubmitExamResponse;
import com.arishi.AXAM.model.AttemptQuestion;
import com.arishi.AXAM.model.ExamAttempt;
import com.arishi.AXAM.model.Question;
import org.springframework.stereotype.Component;

@Component
public class ExamAttemptMapper {

    public StartExamResponse.ExamQuestionDTO toExamQuestionDTO(Question question, int displayOrder) {
        return StartExamResponse.ExamQuestionDTO.builder().id(question.getId()).displayOrder(displayOrder).questionContent(question.getQuestionContent()).optionA(question.getOptionA()).optionB(question.getOptionB()).optionC(question.getOptionC()).optionD(question.getOptionD()).difficultyLevel(question.getDifficultyLevel() != null ? question.getDifficultyLevel().toString() : null).imageUrl(question.getImageUrl()).build();
    }


    public AttemptQuestion toAttemptQuestion(ExamAttempt attempt, Question question, int displayOrder) {
        return AttemptQuestion.builder().examAttempt(attempt).question(question).displayOrder(displayOrder).answered(false).selectedAnswer(null).build();
    }


    public SubmitExamResponse.QuestionResultDTO toQuestionResultDTO(AttemptQuestion attemptQuestion) {
        return SubmitExamResponse.QuestionResultDTO.builder().displayOrder(attemptQuestion.getDisplayOrder()).questionContent(attemptQuestion.getQuestion().getQuestionContent()).userAnswer(attemptQuestion.getSelectedAnswer() != null ? attemptQuestion.getSelectedAnswer() : "Not Answered").correctAnswer(attemptQuestion.getQuestion().getCorrectAnswer()).isCorrect(attemptQuestion.getIsCorrect()).marksObtained(attemptQuestion.getMarksObtained()).difficultyLevel(attemptQuestion.getQuestion().getDifficultyLevel() != null ? attemptQuestion.getQuestion().getDifficultyLevel().toString() : null).timeSpent(attemptQuestion.getTimeSpentInSeconds() != null ? attemptQuestion.getTimeSpentInSeconds().longValue() : null).build();
    }
}