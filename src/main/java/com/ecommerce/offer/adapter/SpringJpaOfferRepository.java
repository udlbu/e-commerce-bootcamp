package com.ecommerce.offer.adapter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface SpringJpaOfferRepository extends CrudRepository<OfferEntity, Long>, QuerydslPredicateExecutor<OfferEntity> {
    @Query(value = "SELECT COUNT(*) FROM app.ORDER_LINES l WHERE l.OFFER_ID = ?1", nativeQuery = true)
    Long countOrderLinesWithOffer(Long offerId);
}
