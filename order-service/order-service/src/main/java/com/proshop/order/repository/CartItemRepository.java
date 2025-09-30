package com.proshop.order.repository;

import com.proshop.order.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {

    @Query("SELECT ci FROM CartItemEntity ci " +
            "WHERE ci.cart.userId = :userId AND ci.productId = :productId")
    Optional<CartItemEntity> findByUserIdAndProductId(@Param("userId") UUID userId,
                                                      @Param("productId") UUID productId);

    @Query("SELECT ci FROM CartItemEntity ci WHERE ci.cart.userId = :userId")
    List<CartItemEntity> findByUserId(@Param("userId") UUID userId);
}

