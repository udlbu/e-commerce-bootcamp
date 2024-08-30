package com.ecommerce.cart.adapter;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpringJpaCartRepository extends CrudRepository<CartEntity, Long> {

    Optional<CartEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query(value = "SELECT c FROM CartEntity c WHERE c.id = ?1")
    CartEntity findAndLockCartById(Long cartId);

    void deleteByUserId(Long userId);
}
