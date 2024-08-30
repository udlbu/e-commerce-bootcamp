package com.ecommerce.order.adapter;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;

public interface SpringJpaOrderRepository extends CrudRepository<OrderEntity, Long>, QuerydslPredicateExecutor<OrderEntity> {

    List<OrderEntity> findByDeliveryStatusNotAndUpdatedAtLessThan(String deliveryStatus, Instant date);
}
