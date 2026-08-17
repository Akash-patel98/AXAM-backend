package com.arishi.AXAM.repo;

import com.arishi.AXAM.enums.ExamAttemptStatus;
import com.arishi.AXAM.model.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {


    Optional<ExamAttempt> findByUserIdAndStatus(Long userId, ExamAttemptStatus status);

    // Check if user has an active exam attempt
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM ExamAttempt e WHERE e.user.id = :userId AND e.status = 'IN_PROGRESS'")
    boolean existsActiveAttemptForUser(@Param("userId") Long userId);


    List<ExamAttempt> findByUserId(Long userId);

    Page<ExamAttempt> findByUserId(Long userId, Pageable pageable);


    List<ExamAttempt> findByUserIdAndExamId(Long userId, Long examId);

    // Check if user passed this exam
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM ExamAttempt e WHERE e.user.id = :userId AND e.exam.id = :examId " +
            "AND e.status = 'SUBMITTED' AND e.percentage >= 40")
    boolean hasUserPassedExam(@Param("userId") Long userId, @Param("examId") Long examId);


    // Get attempts within time range (for analytics)
    @Query("SELECT e FROM ExamAttempt e WHERE e.startAt >= :startTime AND e.startAt <= :endTime")
    List<ExamAttempt> findAttemptsInTimeRange(@Param("startTime") Instant startTime,
                                              @Param("endTime") Instant endTime);

    // Get active attempts (for cleanup/monitoring)
    List<ExamAttempt> findByStatus(ExamAttemptStatus status);

    //Count attempts for a user on a specific exam scheduler
    @Query("SELECT COUNT(e) FROM ExamAttempt e WHERE e.user.id = :userId AND e.scheduler.id = :schedulerId")
    int countUserAttemptsForScheduler(@Param("userId") Long userId, @Param("schedulerId") Long schedulerId);
}
