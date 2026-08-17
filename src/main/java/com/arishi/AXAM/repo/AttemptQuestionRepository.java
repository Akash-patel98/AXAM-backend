package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.AttemptQuestion;
import com.arishi.AXAM.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, Long> {

    List<AttemptQuestion> findByExamAttempt(ExamAttempt examAttempt);

    Optional<AttemptQuestion> findByExamAttemptAndDisplayOrder(ExamAttempt examAttempt, Integer displayOrder);


     // Get answered questions for an attempt
    @Query("SELECT aq FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt AND aq.answered = true")
    List<AttemptQuestion> findAnsweredQuestions(@Param("attempt") ExamAttempt attempt);


     //Get unanswered questions for an attempt
    @Query("SELECT aq FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt AND aq.answered = false")
    List<AttemptQuestion> findUnansweredQuestions(@Param("attempt") ExamAttempt attempt);

    @Query("SELECT aq FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt AND aq.isCorrect = true")
    List<AttemptQuestion> findCorrectAnswers(@Param("attempt") ExamAttempt attempt);


     // Count answered questions for an attempt
    @Query("SELECT COUNT(aq) FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt AND aq.answered = true")
    int countAnsweredQuestions(@Param("attempt") ExamAttempt attempt);

    @Query("SELECT COUNT(aq) FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt AND aq.isCorrect = true")
    int countCorrectAnswers(@Param("attempt") ExamAttempt attempt);

    @Query("SELECT COALESCE(SUM(aq.marksObtained), 0) FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt")
    int sumMarksObtained(@Param("attempt") ExamAttempt attempt);

    @Query("SELECT COALESCE(SUM(aq.timeSpentInSeconds), 0) FROM AttemptQuestion aq WHERE aq.examAttempt = :attempt")
    long sumTimeSpent(@Param("attempt") ExamAttempt attempt);


     // Check if a question has been answered
    @Query("SELECT CASE WHEN COUNT(aq) > 0 THEN true ELSE false END FROM AttemptQuestion aq " +
            "WHERE aq.examAttempt = :attempt AND aq.displayOrder = :displayOrder AND aq.answered = true")
    boolean isQuestionAnswered(@Param("attempt") ExamAttempt attempt, @Param("displayOrder") Integer displayOrder);
}