package com.proshop.product.repository;

import com.proshop.product.entity.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SKURepository extends JpaRepository<SKUEntity, UUID> {
}

