package com.ecommerce.product.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringJpaProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = "SELECT COUNT(*) FROM app.OFFERS o WHERE EXISTS (SELECT 1 FROM app.PRODUCTS p WHERE p.ID = o.PRODUCT_ID AND p.ID = ?1)", nativeQuery = true)
    Long countOffersWithProduct(Long productId);

    @Query(value = "SELECT COUNT(*) FROM app.ORDER_LINES l JOIN app.OFFERS o ON o.ID = l.OFFER_ID WHERE o.PRODUCT_ID = ?1", nativeQuery = true)
    Long countOrderLinesWithProduct(Long productId);
}
