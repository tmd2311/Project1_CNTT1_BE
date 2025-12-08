package com.proshop.order.entity;
public enum OrderStatus
{
    COMPLETED,    // Đã hoàn thành
    PENDING,      // Đang chờ thanh toán
    PROCESSING,   // Đang xử lý (sau khi payment processing)
    CONFIRMED,    // Đã xác nhận (sau khi trừ stock)
    SHIPPING,     // Đang giao hàng
    DELIVERED,    // Đã giao hàng
    CANCELLED,    // Đã hủy
    RETURNED      // Đã trả hàng
}