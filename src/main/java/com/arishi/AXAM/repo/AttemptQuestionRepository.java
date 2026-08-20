package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.AttemptQuestion;
import com.arishi.AXAM.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptQuestionRepository extends JpaRepository<AttemptQuestion, Long> {

    List<AttemptQuestion> findByExamAttempt(ExamAttempt examAttempt);

    Optional<AttemptQuestion> findByExamAttemptAndDisplayOrder(ExamAttempt examAttempt, Integer displayOrder);

    int countByExamAttemptAndAnsweredTrue(ExamAttempt examAttempt);

    int countByExamAttemptAndIsCorrectTrue(ExamAttempt examAttempt);

}