package com.proshop.review_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Laptop, PC, Gaming, Smartphone

    @Column(length = 100)
    private String slug; // laptop, pc-gaming

    private String description;

    private String icon; // Icon URL hoặc emoji 💻

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer displayOrder = 0;
}