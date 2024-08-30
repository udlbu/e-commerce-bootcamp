package com.ecommerce.user.domain;

import com.ecommerce.shared.domain.TimeProvider;
import com.ecommerce.user.domain.model.ActivationToken;
import com.ecommerce.user.domain.model.UserId;
import com.ecommerce.user.domain.port.UserActivationRepositoryPort;

import java.util.UUID;

public class ActivationTokenGenerator {

    private final UserActivationRepositoryPort userActivationRepository;

    private final TimeProvider timeProvider;

    public ActivationTokenGenerator(UserActivationRepositoryPort userActivationRepository, TimeProvider timeProvider) {
        this.userActivationRepository = userActivationRepository;
        this.timeProvider = timeProvider;
    }

    public ActivationToken generate(UserId userId) {
        int tries = 3;
        String token = newToken();
        while (tries > 0 && userActivationRepository.tokenExists(token)) {
            token = newToken();
            tries--;
        }
        return new ActivationToken(userId.val(), token, timeProvider.now());
    }

    private String newToken() {
        UUID uuid = UUID.randomUUID();
        return Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
    }
}