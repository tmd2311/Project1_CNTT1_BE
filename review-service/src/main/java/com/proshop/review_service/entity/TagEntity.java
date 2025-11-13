package com.proshop.review_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // "Laptop", "Học tập", "Văn phòng"

    @Column(length = 50)
    private String slug;

    @Column(nullable = false)
    private Integer usageCount = 0; // Số lần được sử dụng
}