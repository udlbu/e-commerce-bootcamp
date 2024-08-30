package com.ecommerce.user.domain.port;

import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.Token;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;

import java.time.Instant;

public interface UserRepositoryPort {

    User findUserById(UserId id);

    User findUserByEmail(Email email);

    void deleteUserById(UserId id);

    User saveUser(User user);

    void activate(Token token, Instant now);

    void updateUser(User user);

    boolean isActive(Email email);
}
