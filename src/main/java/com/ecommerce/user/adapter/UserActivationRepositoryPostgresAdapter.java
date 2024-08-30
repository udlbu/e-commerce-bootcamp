package com.ecommerce.user.adapter;

import com.ecommerce.user.domain.model.ActivationToken;
import com.ecommerce.user.domain.model.UserId;
import com.ecommerce.user.domain.port.UserActivationRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserActivationRepositoryPostgresAdapter implements UserActivationRepositoryPort {

    private final SpringJpaUserActivationRepository springJpaUserActivationRepository;

    public UserActivationRepositoryPostgresAdapter(SpringJpaUserActivationRepository springJpaUserActivationRepository) {
        this.springJpaUserActivationRepository = springJpaUserActivationRepository;
    }

    @Override
    public boolean tokenExists(String token) {
        return springJpaUserActivationRepository.countTokens(token) > 0;
    }

    @Override
    public void saveActivation(ActivationToken activationToken) {
        UserActivationEntity activationEntity = UserActivationEntity.newActivation(activationToken);
        springJpaUserActivationRepository.save(activationEntity);
    }

    @Override
    public void deleteByUserId(UserId userId) {
        springJpaUserActivationRepository.deleteByUserId(userId.val());
    }
}
