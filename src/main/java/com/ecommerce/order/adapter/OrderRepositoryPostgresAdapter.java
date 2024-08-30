package com.ecommerce.order.adapter;

import com.ecommerce.offer.adapter.OfferEntity;
import com.ecommerce.offer.adapter.QOfferEntity;
import com.ecommerce.offer.adapter.SpringJpaOfferRepository;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.order.domain.DeliveryStatus;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderId;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.order.domain.SearchOrder;
import com.ecommerce.order.domain.port.OrderRepositoryPort;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.shared.domain.TimeProvider;
import com.ecommerce.shared.domain.TotalSize;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Transactional
public class OrderRepositoryPostgresAdapter implements OrderRepositoryPort {

    private final SpringJpaOrderRepository springJpaOrderRepository;

    private final SpringJpaOfferRepository springJpaOfferRepository;

    private final TimeProvider timeProvider;

    public OrderRepositoryPostgresAdapter(SpringJpaOrderRepository springJpaOrderRepository,
                                          SpringJpaOfferRepository springJpaOfferRepository,
                                          TimeProvider timeProvider) {
        this.springJpaOrderRepository = springJpaOrderRepository;
        this.springJpaOfferRepository = springJpaOfferRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public PageResult<Order> findOrders(SearchOrder searchOrder) {
        QOrderEntity order = QOrderEntity.orderEntity;
        BooleanExpression where = Expressions.TRUE;
        where = where.and(order.buyerId.eq(searchOrder.userId().val()));
        Page<OrderEntity> ordersPage = springJpaOrderRepository.findAll(where, toPageRequest(searchOrder));
        return new PageResult<>(ordersPage.map(OrderEntity::toDomain).toList(), new TotalSize(ordersPage.getTotalElements()));
    }

    @Override
    public Order findOrderById(OrderId orderId) {
        return springJpaOrderRepository.findById(orderId.val())
                .map(OrderEntity::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteOrderById(OrderId orderId) {
        springJpaOrderRepository.deleteById(orderId.val());
    }

    @Override
    public OrderId saveOrder(Order order) {
        QOfferEntity offer = QOfferEntity.offerEntity;
        BooleanExpression where = Expressions.TRUE;
        where = where.and(offer.id.in(order.offerIds().stream().map(OfferId::val).toList()));
        where = where.and(offer.status.eq(OfferStatus.ACTIVE.name()));
        Iterable<OfferEntity> offers = springJpaOfferRepository.findAll(where);
        OrderEntity orderEntity = OrderEntity.newOrder(order, offers);
        OrderEntity saved = springJpaOrderRepository.save(orderEntity);
        return new OrderId(saved.getId());
    }

    @Override
    public void changeOrderStatus(OrderId orderId, OrderStatus orderStatus) {
        springJpaOrderRepository.findById(orderId.val())
                .ifPresent(it -> it.changeStatus(orderStatus, timeProvider.now()));
    }

    @Override
    public void changeOrderDeliveryStatus() {
        Instant now = timeProvider.now();
        List<OrderEntity> orders = springJpaOrderRepository.findByDeliveryStatusNotAndUpdatedAtLessThan(
                DeliveryStatus.DELIVERED.name(),
                now.minus(5, ChronoUnit.MINUTES));
        orders.forEach(it -> it.changeDeliveryStatus(DeliveryStatus.valueOf(it.getDeliveryStatus()).next(), now));
    }


    private PageRequest toPageRequest(SearchOrder searchOrder) {
        return PageRequest.of(searchOrder.pageNumber().val(), searchOrder.pageSize().val());
    }
}
