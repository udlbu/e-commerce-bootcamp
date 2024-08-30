package com.ecommerce.order.domain.port;

import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderId;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.order.domain.SearchOrder;
import com.ecommerce.shared.domain.PageResult;

public interface OrderRepositoryPort {

     PageResult<Order> findOrders(SearchOrder searchOrder);

     Order findOrderById(OrderId orderId);

     void deleteOrderById(OrderId orderId);

     OrderId saveOrder(Order order);

    void changeOrderStatus(OrderId orderId, OrderStatus orderStatus);

    void changeOrderDeliveryStatus();
}
