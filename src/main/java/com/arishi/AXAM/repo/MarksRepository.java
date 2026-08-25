package com.arishi.AXAM.repo;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {
    Optional<Marks> findByExamIdAndDifficultyLevel(Long examId, DifficultyLevel difficultyLevel);
    List<Marks> findByExamId(Long examId);
}