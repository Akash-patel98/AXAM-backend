package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam , Long> {

    boolean existsByTitleIgnoreCaseAndDeletedAtIsNull( String title);

    Optional<Exam> findByTitleIgnoreCaseAndDeletedAtIsNull(String title);

    Optional<Exam> findByIdAndDeletedAtIsNull(Long id);

    List<Exam> findByDeletedAtIsNull();
}
