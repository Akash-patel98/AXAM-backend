package com.arishi.AXAM.model;

import com.arishi.AXAM.enums.ExamAttemptStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ExamAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduler_id", nullable = false)
    private ExamScheduler scheduler;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Instant startAt;

    private Instant endAt;

    private Integer totalQuestions;

    private Integer attemptedQuestions;

    private Integer unattemptedQuestions;

    private Integer correctAnswers;

    private Integer incorrectAnswers;

    private Integer obtainedMarks;

    private Integer totalMarks;

    private Float percentage;

    @Enumerated(EnumType.STRING)
    private ExamAttemptStatus status;

    @Column(unique = true)
    private String activeSessionId;  // which tab is active

    @Column(nullable = false)
    private Instant lastActivityAt;
}