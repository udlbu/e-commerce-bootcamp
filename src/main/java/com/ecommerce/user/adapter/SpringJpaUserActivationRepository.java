
package com.ecommerce.user.adapter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SpringJpaUserActivationRepository extends CrudRepository<UserActivationEntity, Long> {

    @Query(value = "SELECT COUNT(*) FROM app.USER_ACTIVATIONS l WHERE l.TOKEN = ?1", nativeQuery = true)
    Long countTokens(String token);

    @Query(value = "SELECT a FROM UserActivationEntity a WHERE a.token = ?1 AND a.status = 'NEW'")
    UserActivationEntity findNewToken(String token);

    void deleteByUserId(Long userId);
}
