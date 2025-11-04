package com.proshop.product.repository;

import com.proshop.product.entity.CategoryEntity;
import com.proshop.product.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    // ========== BASIC QUERIES ==========

    Optional<CategoryEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID id);
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);

    // ========== HIERARCHY QUERIES ==========

    List<CategoryEntity> findByParentIsNullOrderByName();
    List<CategoryEntity> findByParentIdOrderByName(UUID parentId);
    List<CategoryEntity> findByParentOrderByName(CategoryEntity parent);

    @Query("""
        SELECT c
        FROM CategoryEntity c
        ORDER BY c.parent.id ASC NULLS FIRST, c.name ASC
        """)
    List<CategoryEntity> findAllByOrderByParentIdAscNameAsc();

    // ========== SEARCH WITH PAGINATION ==========

    /**
     * Search categories with name filter and pagination
     */
    @Query("""
    SELECT new com.proshop.product.dto.response.CategoryResponse(
        c.id,
        c.name,
        c.slug,
        c.parent.id,
        c.parent.name,
        c.parent.slug,
        SIZE(c.children),
        CASE WHEN SIZE(c.children) > 0 THEN true ELSE false END,
        CASE
            WHEN c.parent IS NULL THEN 1
            WHEN c.parent.parent IS NULL THEN 2
            ELSE 3
        END,
        CASE
            WHEN c.parent IS NULL THEN 'ROOT'
            WHEN c.parent.parent IS NULL THEN
                CASE
                    WHEN LOWER(c.parent.name) LIKE '%laptop%' THEN 'LAPTOP_TYPE'
                    WHEN LOWER(c.parent.name) LIKE '%desktop%' OR LOWER(c.parent.name) LIKE '%pc%' THEN 'PC_CATEGORY'
                    WHEN LOWER(c.parent.name) LIKE '%component%' THEN 'COMPONENT_TYPE'
                    WHEN LOWER(c.parent.name) LIKE '%peripheral%' THEN 'PERIPHERAL_TYPE'
                    ELSE 'SUBCATEGORY'
                END
            ELSE 'COMPONENT_SUBTYPE'
        END,
        0L,
        true,
        c.imageUrl
    )
    FROM CategoryEntity c
    WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    ORDER BY c.parent.name ASC NULLS FIRST, c.name ASC
    """)
    Page<CategoryResponse> searchCategories(@Param("name") String name, Pageable pageable);

    // ========== HIERARCHY SPECIFIC QUERIES ==========

    @Query(value = """
        WITH RECURSIVE category_tree AS (
            SELECT id, name, slug, parent_id, 1 as level
            FROM category WHERE id = :categoryId
            UNION ALL
            SELECT c.id, c.name, c.slug, c.parent_id, ct.level + 1
            FROM category c
            INNER JOIN category_tree ct ON c.parent_id = ct.id
        )
        SELECT c.* FROM category c
        WHERE c.id IN (SELECT id FROM category_tree WHERE level > 1)
        """, nativeQuery = true)
    List<CategoryEntity> findAllDescendants(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE CASE 
                WHEN c.parent IS NULL THEN 1
                WHEN c.parent.parent IS NULL THEN 2  
                ELSE 3
              END = :level
        """)
    List<CategoryEntity> findByHierarchyLevel(@Param("level") int level);

    // ========== PC STORE SPECIFIC QUERIES ==========

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE c.parent.slug = 'laptops' 
        ORDER BY c.name
        """)
    List<CategoryEntity> findLaptopCategories();

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE c.parent.slug = 'pc-components' 
        ORDER BY c.name
        """)
    List<CategoryEntity> findComponentCategories();

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE c.parent.slug = 'peripherals' 
        ORDER BY c.name
        """)
    List<CategoryEntity> findPeripheralCategories();

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE c.parent.slug = 'desktop-pcs' OR c.parent.parent.slug = 'desktop-pcs' 
        ORDER BY c.name
        """)
    List<CategoryEntity> findDesktopPCCategories();

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE c.parent.slug = 'storage' OR c.parent.parent.slug = 'storage' 
        ORDER BY c.name
        """)
    List<CategoryEntity> findStorageCategories();

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE c.parent.slug = 'cooling-systems' OR c.parent.parent.slug = 'cooling-systems' 
        ORDER BY c.name
        """)
    List<CategoryEntity> findCoolingCategories();

    // ========== CATEGORY TYPE QUERIES ==========

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE (:type = 'ROOT' AND c.parent IS NULL) OR
              (:type = 'LAPTOP_TYPE' AND c.parent.slug = 'laptops') OR
              (:type = 'PC_CATEGORY' AND c.parent.slug = 'desktop-pcs') OR
              (:type = 'COMPONENT_TYPE' AND c.parent.slug = 'pc-components') OR
              (:type = 'PERIPHERAL_TYPE' AND c.parent.slug = 'peripherals') OR
              (:type = 'COMPONENT_SUBTYPE' AND c.parent.parent.slug = 'pc-components') OR
              (:type = 'PC_TYPE' AND c.parent.parent.slug = 'desktop-pcs')
        """)
    List<CategoryEntity> findByTypeWithPagination(@Param("type") String type, Pageable pageable);

    // ========== STATISTICS QUERIES ==========

    @Query("""
        SELECT COUNT(p)
        FROM ProductEntity p 
        WHERE p.category.id = :categoryId
        """)
    Long countProductsInCategory(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT COUNT(p)
        FROM ProductEntity p
        WHERE p.category.id = :categoryId OR
              p.category.parent.id = :categoryId OR
              p.category.parent.parent.id = :categoryId
        """)
    Long countProductsInCategoryTree(@Param("categoryId") UUID categoryId);

    Long countByParentId(UUID parentId);
    Long countByParentIsNull();

    @Query("""
        SELECT MAX(
            CASE
                WHEN c.parent IS NULL THEN 1
                WHEN c.parent.parent IS NULL THEN 2
                ELSE 3
            END
        )
        FROM CategoryEntity c
        """)
    Integer getMaxHierarchyDepth();

    // ========== VALIDATION QUERIES ==========

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM CategoryEntity c
        WHERE c.parent.id = :categoryId
        """)
    boolean hasChildren(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM ProductEntity p
        WHERE p.category.id = :categoryId
        """)
    boolean hasProducts(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT c
        FROM CategoryEntity c
        WHERE NOT EXISTS (
            SELECT 1 FROM CategoryEntity child
            WHERE child.parent = c
        )
        """)
    List<CategoryEntity> findLeafCategories();

    @Query("""
        SELECT c
        FROM CategoryEntity c
        WHERE NOT EXISTS (
            SELECT 1 FROM ProductEntity p 
            WHERE p.category = c
        )
        """)
    List<CategoryEntity> findEmptyCategories();

    // ========== BULK OPERATIONS ==========

    @Query("""
        SELECT c, SIZE(c.children) as childrenCount
        FROM CategoryEntity c
        ORDER BY c.name
        """)
    List<Object[]> findAllWithChildrenCount();

    List<CategoryEntity> findByNameIn(List<String> names);
    List<CategoryEntity> findBySlugIn(List<String> slugs);

    // ========== UTILITY QUERIES ==========

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, UUID id);

    @Query(value = """
        SELECT * FROM category 
        WHERE parent_id IS NULL 
        ORDER BY RANDOM() 
        LIMIT :limit
        """, nativeQuery = true)
    List<CategoryEntity> findRandomRootCategories(@Param("limit") int limit);

    @Query("""
        SELECT c 
        FROM CategoryEntity c 
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND
              (:parentId IS NULL OR c.parent.id = :parentId) AND
              (:hasChildren IS NULL OR 
               (:hasChildren = true AND SIZE(c.children) > 0) OR 
               (:hasChildren = false AND SIZE(c.children) = 0))
        """)
    Page<CategoryEntity> findByCriteria(
        @Param("name") String name,
        @Param("parentId") UUID parentId,
        @Param("hasChildren") Boolean hasChildren,
        Pageable pageable
    );

    @Query(value = """
        WITH RECURSIVE category_path AS (
            SELECT id, name, slug, parent_id, 0 as level
            FROM category WHERE id = :categoryId
            UNION ALL
            SELECT c.id, c.name, c.slug, c.parent_id, cp.level + 1
            FROM category c
            INNER JOIN category_path cp ON c.id = cp.parent_id
        )
        SELECT c.* FROM category c
        WHERE c.id IN (SELECT id FROM category_path)
        ORDER BY (SELECT level FROM category_path WHERE category_path.id = c.id) DESC
        """, nativeQuery = true)
    List<CategoryEntity> getCategoryPath(@Param("categoryId") UUID categoryId);

    List<CategoryEntity> findByParentIsNull(Sort sort);
    List<CategoryEntity> findByParentId(UUID parentId, Sort sort);

    @Query("""
    SELECT c 
    FROM CategoryEntity c 
    WHERE c.parent IS NOT NULL AND LOWER(c.parent.name) LIKE LOWER(CONCAT('%', :rootName, '%'))
    ORDER BY c.name
    """)
    List<CategoryEntity> findCategoriesByRootName(@Param("rootName") String rootName);

    @Query("""
    SELECT c 
    FROM CategoryEntity c 
    WHERE c.parent IS NOT NULL AND LOWER(c.parent.name) LIKE LOWER(CONCAT('%', :rootName, '%'))
    """)
    Page<CategoryEntity> findCategoriesByRootName(@Param("rootName") String rootName, Pageable pageable);
}
