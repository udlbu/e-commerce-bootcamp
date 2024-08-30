package com.ecommerce.user;

import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.mail.domain.model.ActivationMail;
import com.ecommerce.shared.domain.TimeProvider;
import com.ecommerce.user.adapter.keycloak.KeycloakIAMAdapter;
import com.ecommerce.user.domain.ActivationTokenGenerator;
import com.ecommerce.user.domain.exceptions.PasswordDoesNotMatchException;
import com.ecommerce.user.domain.model.ActivationToken;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.Token;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import com.ecommerce.user.domain.port.IAMPort;
import com.ecommerce.user.domain.port.UserActivationRepositoryPort;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import jakarta.ws.rs.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacade {

    private final static Logger LOG = LoggerFactory.getLogger(KeycloakIAMAdapter.class);

    private final UserRepositoryPort userRepository;

    private final UserActivationRepositoryPort userActivationRepository;

    private final IAMPort iamPort;

    private final ActivationTokenGenerator generator;

    private final TimeProvider timeProvider;

    private final ActivationEmailSender mailSender;

    public UserFacade(UserRepositoryPort userRepository, UserActivationRepositoryPort userActivationRepository, IAMPort iamPort, ActivationTokenGenerator generator, TimeProvider timeProvider, ActivationEmailSender mailSender) {
        this.userRepository = userRepository;
        this.userActivationRepository = userActivationRepository;
        this.iamPort = iamPort;
        this.generator = generator;
        this.timeProvider = timeProvider;
        this.mailSender = mailSender;
    }

    public User getUser(UserId id) {
        return userRepository.findUserById(id);
    }

    public User addUser(User user, Password password) {
        iamPort.addUser(user, password);
        try {
            User newUser = userRepository.saveUser(user);
            ActivationToken activationToken = generator.generate(newUser.id());
            userActivationRepository.saveActivation(activationToken);
            mailSender.send(ActivationMail.of(user.firstName().val(), user.email().val(), activationToken.token()));
            return newUser;
        } catch (Exception ex) {
            LOG.error("User creation failed: ", ex);
            iamPort.deleteUser(user.email());
            throw ex;
        }
    }

    public void activateAccount(Token token) {
        userRepository.activate(token, timeProvider.now());
    }

    public void modifyUser(User user) {
        userRepository.updateUser(user);
    }

    public void deleteUser(UserId userId) {
        iamPort.deleteUser(userId);
        userActivationRepository.deleteByUserId(userId);
        userRepository.deleteUserById(userId);
    }

    public void changePassword(UserId userId, Password oldPassword, Password newPassword) {
        User user = userRepository.findUserById(userId);
        try {
            iamPort.authenticate(user.email().val(), oldPassword.val());
        } catch (NotAuthorizedException ex) {
            throw new PasswordDoesNotMatchException("User old password does not match password");
        }
        iamPort.changePassword(user, newPassword);
    }
}
