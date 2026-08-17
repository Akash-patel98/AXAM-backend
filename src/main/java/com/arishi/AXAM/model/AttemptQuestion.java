package com.arishi.AXAM.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AttemptQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean answered;

    @Column(length = 1)
    private String selectedAnswer;

    private Boolean isCorrect;

    @Builder.Default
    private Integer marksObtained = 0;

    private Integer timeSpentInSeconds;

    private Instant answeredAt;
}