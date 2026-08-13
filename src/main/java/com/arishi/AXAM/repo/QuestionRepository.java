package com.arishi.AXAM.repo;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.enums.QuestionsStatus;
import com.arishi.AXAM.model.Question;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsByQuestionContentIgnoreCaseAndCategoryIdAndDeletedAtIsNull(String questionContent, Long categoryId);

    List<Question> findAllByDeletedAtIsNull();

    List<Question> findAllByCategoryTitleIgnoreCaseAndDeletedAtIsNull(String categoryTitle);

    List<Question> findAllByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(long id, DifficultyLevel difficultyLevel);

    Optional<Question> findByIdAndDeletedAtIsNull(Long id);

    long countByCategoryIdAndDifficultyLevelAndDeletedAtIsNull(Long categoryId, DifficultyLevel difficultyLevel);

  }
