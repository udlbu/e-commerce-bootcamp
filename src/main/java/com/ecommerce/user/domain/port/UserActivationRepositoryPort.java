package com.ecommerce.user.domain.port;

import com.ecommerce.user.domain.model.ActivationToken;
import com.ecommerce.user.domain.model.UserId;

public interface UserActivationRepositoryPort {

    boolean tokenExists(String token);

    void saveActivation(ActivationToken activationToken);

    void deleteByUserId(UserId userId);
}
