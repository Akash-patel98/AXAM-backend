package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsByQuestionContentIgnoreCaseAndCategoryIdAndDeletedAtIsNull(String questionContent, Long categoryId);
}
