package com.arishi.AXAM.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Exam extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Exam title is required")
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String title;

    @Size(max = 250, message = "Description max length 250")
    private String description;

    @Size(max = 500, message = "Instruction max length 500")
    private String instruction;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Float passingPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id", nullable = false)
    private BluePrint bluePrint;
}