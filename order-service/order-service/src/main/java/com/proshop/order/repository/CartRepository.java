package com.proshop.order.repository;

import com.proshop.order.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {

    /**
     * Tìm giỏ hàng theo userId
     */
    Optional<CartEntity> findByUserId(long userId);

    /**
     * Kiểm tra user đã có giỏ hàng chưa
     */
    boolean existsByUserId(long userId);
}