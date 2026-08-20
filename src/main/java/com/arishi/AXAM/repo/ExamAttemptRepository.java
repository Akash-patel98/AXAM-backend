package com.arishi.AXAM.repo;

import com.arishi.AXAM.enums.ExamAttemptStatus;
import com.arishi.AXAM.model.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Optional<ExamAttempt> findByUserIdAndStatus(Long userId, ExamAttemptStatus status);

    boolean existsByUserIdAndStatus(Long userId, ExamAttemptStatus status);

    List<ExamAttempt> findByUserId(Long userId);

    Page<ExamAttempt> findByUserId(Long userId, Pageable pageable);

    int countByUserIdAndSchedulerId(Long userId, Long schedulerId);

    List<ExamAttempt> findBySchedulerIdAndStatus(long id, ExamAttemptStatus examAttemptStatus);
}