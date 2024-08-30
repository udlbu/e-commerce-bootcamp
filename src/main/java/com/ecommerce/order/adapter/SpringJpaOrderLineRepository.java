package com.ecommerce.order.adapter;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpringJpaOrderLineRepository extends CrudRepository<OrderLineEntity, Long> {

    List<OrderLineEntity> findByOrderId(Long orderId);
}
