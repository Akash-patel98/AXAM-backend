package com.arishi.AXAM.model;

import com.arishi.AXAM.enums.BluePrintStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BluePrint extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Blueprint title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Size(max = 250, message = "Description max length 250")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BluePrintStatus bluePrintStatus ;
}
