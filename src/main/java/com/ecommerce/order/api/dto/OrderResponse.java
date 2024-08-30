package com.ecommerce.order.api.dto;

import com.ecommerce.order.domain.Order;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private String deliveryMethod;
    private String deliveryStatus;
    private String paymentMethod;
    private String paymentStatus;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
    private List<OrderLineResponse> lines;

    public static OrderResponse of(Order order, String cdnUrl) {
        OrderResponse response = new OrderResponse();
        response.setId(order.id().val());
        response.setDeliveryMethod(order.deliveryMethod().name());
        response.setDeliveryStatus(order.deliveryStatus().name());
        response.setPaymentMethod(order.paymentMethod().name());
        response.setPaymentStatus(order.paymentStatus().name());
        response.setStatus(order.status().name());
        response.setCreatedAt(order.createdAt().val());
        response.setUpdatedAt(order.updatedAt().val());
        response.setVersion(order.version().val());
        response.setLines(order.lines().stream().map(line -> OrderLineResponse.of(line, cdnUrl)).toList());
        return response;
    }
}
