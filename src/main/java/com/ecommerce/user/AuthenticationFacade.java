package com.ecommerce.user;

import com.ecommerce.user.domain.exceptions.InactiveAccountException;
import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.authentication.AccessToken;
import com.ecommerce.user.domain.model.authentication.Principal;
import com.ecommerce.user.domain.port.IAMPort;
import com.ecommerce.user.domain.port.UserRepositoryPort;

public class AuthenticationFacade {

    private final IAMPort iamPort;

    private final UserRepositoryPort userRepository;

    public AuthenticationFacade(IAMPort iamPort, UserRepositoryPort userRepository) {
        this.iamPort = iamPort;
        this.userRepository = userRepository;
    }

    public Principal authenticate(Email email, Password password) {
        if (!userRepository.isActive(email)) {
            throw new InactiveAccountException("Account <" + email.val() + "> is inactive");
        }
        AccessToken accessToken = iamPort.authenticate(email.val(), password.val());
        return new Principal(email.val(), accessToken);
    }

    public void logout(User user) {
        iamPort.logout(user);
    }
}
