package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.ExamScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSchedulerRepository extends JpaRepository<ExamScheduler, Long> {

    boolean existsByExamIdAndStartDateAndEndDate(Long examId, Instant startDate, Instant endDate);

    List<ExamScheduler> findAllByDeletedAtIsNullOrderByStartDateAsc();

    Optional<ExamScheduler> findByIdAndDeletedAtIsNull(Long id);
}