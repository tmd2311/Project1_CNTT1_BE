package com.proshop.order.controller;

import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.OrderDeleteResponse;
import com.proshop.order.dto.response.OrderResponse;
import com.proshop.order.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public GeneralResponse<List<OrderResponse>> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public GeneralResponse<OrderResponse> getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public GeneralResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @PutMapping("/{id}")
    public GeneralResponse<OrderResponse> updateOrder(@PathVariable UUID id, @RequestBody OrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/{id}")
    public GeneralResponse<OrderDeleteResponse> deleteOrder(@PathVariable UUID id) {
        return orderService.deleteOrder(id);
    }
}
