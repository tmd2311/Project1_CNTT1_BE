package com.proshop.order.service.order;

import com.proshop.order.dto.request.OrderCreateRequest;
import com.proshop.order.dto.request.OrderRequest;
import com.proshop.order.dto.response.GeneralResponse;
import com.proshop.order.dto.response.OrderDeleteResponse;
import com.proshop.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    GeneralResponse<List<OrderResponse>> getAllOrders();

    GeneralResponse<OrderResponse> getOrderById(UUID id);

    GeneralResponse<OrderResponse> createOrder(OrderCreateRequest request);

    GeneralResponse<OrderResponse> updateOrder(UUID id, OrderRequest request);

    GeneralResponse<OrderDeleteResponse> deleteOrder(UUID id);
}

