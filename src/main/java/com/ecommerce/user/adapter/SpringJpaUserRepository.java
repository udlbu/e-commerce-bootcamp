package com.ecommerce.user.adapter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpringJpaUserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM app.USERS u WHERE u.EMAIL = ?1 and u.status = 'ACTIVE'", nativeQuery = true)
    Integer countByEmailAndStatusActive(String email);

}
