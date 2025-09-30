package com.proshop.order.service.order.impl;

import com.proshop.order.client.ProductClient;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.OrderDeleteResponse;
import com.proshop.order.dto.response.OrderResponse;
import com.proshop.order.dto.response.ResponseStatus;
import com.proshop.order.entity.OrderEntity;
import com.proshop.order.entity.OrderStatus;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient; // để sau này gọi sang Product Service

    @Override
    public GeneralResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> data = orderRepository.findAll().stream()
                .map(o -> new OrderResponse(
                        o.getOrderId(),
                        o.getUserId(),
                        o.getTotalAmount(),
                        o.getStatus().name(),
                        o.getCreatedAt()
                ))
                .toList();

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    public GeneralResponse<OrderResponse> getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(order -> new GeneralResponse<>(
                        ResponseStatus.SUCCESS_STATUS,
                        new OrderResponse(
                                order.getOrderId(),
                                order.getUserId(),
                                order.getTotalAmount(),
                                order.getStatus().name(),
                                order.getCreatedAt()
                        ),
                        null
                ))
                .orElseGet(() -> new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                        null,
                        null
                ));
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> createOrder(OrderRequest request) {
        OrderEntity order = OrderEntity.builder()
                .userId(request.getUserId())
                .totalAmount(request.getTotalAmount())
                .status(OrderStatus.PENDING) // ✅ dùng enum thay vì String
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        OrderResponse data = new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getCreatedAt()
        );

        return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
    }

    @Override
    @Transactional
    public GeneralResponse<OrderResponse> updateOrder(UUID id, OrderRequest request) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setTotalAmount(request.getTotalAmount());
                    order.setStatus(OrderStatus.PENDING); // ✅ convert string sang enum
                    orderRepository.save(order);

                    OrderResponse data = new OrderResponse(
                            order.getOrderId(),
                            order.getUserId(),
                            order.getTotalAmount(),
                            order.getStatus().name(),
                            order.getCreatedAt()
                    );

                    return new GeneralResponse<>(ResponseStatus.SUCCESS_STATUS, data, null);
                })
                .orElseGet(() -> new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                        null,
                        null
                ));
    }

    @Override
    @Transactional
    public GeneralResponse<OrderDeleteResponse> deleteOrder(UUID id) {
        return orderRepository.findById(id)
                .map(order -> {
                    orderRepository.delete(order);
                    return new GeneralResponse<>(
                            ResponseStatus.SUCCESS_STATUS,
                            new OrderDeleteResponse(order.getOrderId(), order.getUserId()),
                            null
                    );
                })
                .orElseGet(() -> new GeneralResponse<>(
                        new ResponseStatus("404", "Không tìm thấy đơn hàng", "Order Not Found"),
                        null,
                        null
                ));
    }
}
