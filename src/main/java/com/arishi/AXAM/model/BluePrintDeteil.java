package com.arishi.AXAM.model;

import com.arishi.AXAM.enums.DifficultyLevel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class BluePrintDeteil extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "blueprint_id")
    private BluePrint bluePrint;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column(nullable = false)
    private Integer questionCount;  // difficalty level quction count easy 5 quction hard 2
}
