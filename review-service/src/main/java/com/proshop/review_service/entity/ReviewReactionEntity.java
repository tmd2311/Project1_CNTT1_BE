package com.proshop.review_service.entity;

import com.proshop.review_service.util.enums.ReactionTargetType;
import com.proshop.review_service.util.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reactions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"targetType", "targetId", "userId"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionTargetType targetType; // REVIEW hoặc ANSWER

    @Column(nullable = false)
    private Long targetId; // ID của Review hoặc Answer

    @Column(nullable = false)
    private Long userId; // ID người react

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type; // LIKE, DISLIKE, HELPFUL

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
