package com.arishi.AXAM.repo;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    boolean existsByQuestionContentIgnoreCaseAndCategoryIdAndDeletedAtIsNull(String questionContent, Long categoryId);

    List<Question> findAllByDeletedAtIsNull();

    List<Question> findAllByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(Long categoryId, DifficultyLevel difficultyLevel);

    Optional<Question> findByIdAndDeletedAtIsNull(Long id);

    long countByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(Long categoryId, DifficultyLevel difficultyLevel);

}
