package com.proshop.review_service.repository;

import com.proshop.review_service.entity.TagEntity;
import io.micrometer.core.instrument.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    Optional<TagEntity> findByName(String name);

    Optional<TagEntity> findBySlug(String slug);

    List<TagEntity> findByNameIn(List<String> names);

    // Top tags
    @Query("SELECT t FROM TagEntity t ORDER BY t.usageCount DESC LIMIT 20")
    List<Tag> findTop20ByUsageCount();

    @Modifying
    @Query("UPDATE TagEntity t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsageCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE TagEntity t SET t.usageCount = t.usageCount - 1 WHERE t.id = :id AND t.usageCount > 0")
    void decrementUsageCount(@Param("id") Long id);
}