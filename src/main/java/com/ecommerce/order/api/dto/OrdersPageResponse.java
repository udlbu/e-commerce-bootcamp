package com.ecommerce.order.api.dto;

import com.ecommerce.order.domain.Order;
import com.ecommerce.shared.domain.PageResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrdersPageResponse {

    private List<OrderResponse> orders;

    private Long total;

    public static OrdersPageResponse of(PageResult<Order> ordersPage, String cdnUrl) {
        return new OrdersPageResponse(
                ordersPage.data().stream().map(order -> OrderResponse.of(order, cdnUrl)).toList(),
                ordersPage.totalSize().val()
        );
    }
}
