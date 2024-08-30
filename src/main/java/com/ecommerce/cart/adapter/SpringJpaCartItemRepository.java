package com.ecommerce.cart.adapter;

import org.springframework.data.repository.CrudRepository;

public interface SpringJpaCartItemRepository extends CrudRepository<CartItemEntity, Long> {
}
