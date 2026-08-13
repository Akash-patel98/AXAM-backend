package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.ExamScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSchedulerRepository extends JpaRepository<ExamScheduler, Long> {

    boolean existsByExamIdAndStartTimeAndEndTime(Long examId, Instant startTime, Instant endTime);

    List<ExamScheduler> findAllByDeletedAtIsNullOrderByStartTimeAsc();

    Optional<ExamScheduler> findByIdAndDeletedAtIsNull(Long id);
}