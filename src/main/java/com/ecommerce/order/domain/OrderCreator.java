package com.ecommerce.order.domain;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.order.domain.port.OrderRepositoryPort;
import com.ecommerce.shared.domain.TimeProvider;

import java.time.Instant;

public class OrderCreator {

    private final OrderRepositoryPort orderRepository;
    private final TimeProvider timeProvider;

    public OrderCreator(OrderRepositoryPort orderRepository, TimeProvider timeProvider) {
        this.orderRepository = orderRepository;
        this.timeProvider = timeProvider;
    }

    public OrderId create(SubmitOrderCommand submitOrderCommand, Cart cart) {
        Instant now = timeProvider.now();
        Order order =  new Order(
                null,
                cart.userId(),
                submitOrderCommand.deliveryMethod(),
                DeliveryStatus.NEW,
                submitOrderCommand.paymentMethod(),
                PaymentStatus.SUCCESS,
                OrderStatus.NEW,
                new CreatedAt(now),
                new UpdatedAt(now),
                null,
                cart.items().stream().map(it -> new OrderLine(
                        null,
                        it.offer(),
                        it.quantity(),
                        null
                )).toList()
        );
        return orderRepository.saveOrder(order);
    }
}
