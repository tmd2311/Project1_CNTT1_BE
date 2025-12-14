package com.proshop.order.repository;

import com.proshop.order.entity.RevenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueRepository extends JpaRepository<RevenueEntity, Long> {

    /**
     * Find revenue by date
     */
    Optional<RevenueEntity> findByRevenueDate(LocalDate date);

    /**
     * Get revenue records between date range
     */
    @Query("SELECT r FROM RevenueEntity r WHERE r.revenueDate BETWEEN :startDate AND :endDate ORDER BY r.revenueDate ASC")
    List<RevenueEntity> findRevenueByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Sum total revenue in date range
     */
    @Query("SELECT SUM(r.totalRevenue) FROM RevenueEntity r WHERE r.revenueDate BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count total orders in date range
     */
    @Query("SELECT SUM(r.orderCount) FROM RevenueEntity r WHERE r.revenueDate BETWEEN :startDate AND :endDate")
    Long sumOrderCountByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get revenue for current month
     */
    @Query("SELECT r FROM RevenueEntity r WHERE YEAR(r.revenueDate) = :year AND MONTH(r.revenueDate) = :month ORDER BY r.revenueDate ASC")
    List<RevenueEntity> findRevenueByMonth(
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Get revenue for current year
     */
    @Query("SELECT r FROM RevenueEntity r WHERE YEAR(r.revenueDate) = :year ORDER BY r.revenueDate ASC")
    List<RevenueEntity> findRevenueByYear(@Param("year") int year);
}
