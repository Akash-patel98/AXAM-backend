package com.arishi.AXAM.model;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.enums.QuestionsStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Question content is required")
    @Size(max = 1000, message = "Question max length 1000 char")
    @Column(nullable = false, length = 1000)
    private String questionContent;

    private String imageUrl;

    @NotBlank(message = "Option A is required")
    private String optionA;

    @NotBlank(message = "Option B is required")
    private String optionB;

    @NotBlank(message = "Option C is required")
    private String optionC;

    @NotBlank(message = "Option D is required")
    private String optionD;

    @NotNull(message = "Difficulty level is required")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficultyLevel;


    @Pattern(regexp = "[ABCD]", message = "Correct answer must be A, B, C or D")
    @Column(nullable = false, length = 1)
    private String correctAnswer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionsStatus status = QuestionsStatus.ACTIVE;
}


