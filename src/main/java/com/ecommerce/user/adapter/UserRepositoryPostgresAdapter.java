package com.ecommerce.user.adapter;

import com.ecommerce.shared.domain.exception.ConcurrentModification;
import com.ecommerce.user.domain.model.ActivationStatus;
import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.Status;
import com.ecommerce.user.domain.model.Token;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

import static java.lang.String.format;

@Transactional
public class UserRepositoryPostgresAdapter implements UserRepositoryPort {

    private final static Logger LOG = LoggerFactory.getLogger(UserRepositoryPostgresAdapter.class);
    private final SpringJpaUserRepository springJpaUserRepository;

    private final SpringJpaAddressRepository springJpaAddressRepository;

    private final SpringJpaUserActivationRepository springJpaUserActivationRepository;

    public UserRepositoryPostgresAdapter(SpringJpaUserRepository springJpaUserRepository,
                                         SpringJpaAddressRepository springJpaAddressRepository,
                                         SpringJpaUserActivationRepository springJpaUserActivationRepository) {
        this.springJpaUserRepository = springJpaUserRepository;
        this.springJpaAddressRepository = springJpaAddressRepository;
        this.springJpaUserActivationRepository = springJpaUserActivationRepository;
    }

    @Override
    public User findUserById(UserId id) {
        return springJpaUserRepository.findById(id.val())
                .map(UserEntity::toDomain)
                .orElse(null);
    }

    @Override
    public User findUserByEmail(Email email) {
        return springJpaUserRepository.findByEmail(email.val())
                .map(UserEntity::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteUserById(UserId id) {
        springJpaUserRepository.deleteById(id.val());
    }

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = UserEntity.newUser(user);
        return springJpaUserRepository.save(userEntity).toDomain();
    }

    @Override
    public void activate(Token token, Instant now) {
        UserActivationEntity activationEntity = springJpaUserActivationRepository.findNewToken(token.val());
        if (activationEntity == null) {
            LOG.info("Trying to activate account with already activated token or token that does not exist.");
            return;
        }
        activationEntity.setStatus(ActivationStatus.ACTIVATED.name());
        activationEntity.setUpdatedAt(now);
        springJpaUserRepository.findById(activationEntity.getUserId()).ifPresent((userEntity -> {
            userEntity.setStatus(Status.ACTIVE.name());
        }));
    }

    @Override
    public void updateUser(User user) {
        springJpaUserRepository.findById(user.id().val()).ifPresent(entity -> {
            if (!Objects.equals(entity.getVersion(), user.version().val())) {
                throw new ConcurrentModification(format("Stale object: <%s>", user.id()));
            }
            try {
                if (entity.getAddress() != null && user.address() == null) {
                    springJpaAddressRepository.deleteById(entity.getAddress().getId());
                }
                entity.merge(user);
            } catch (ObjectOptimisticLockingFailureException ex) {
                throw new ConcurrentModification(format("Concurrent modification occurred: <%s>", user.id()), ex);
            }
        });
    }

    @Override
    public boolean isActive(Email email) {
        return springJpaUserRepository.countByEmailAndStatusActive(email.val()) > 0;
    }
}
