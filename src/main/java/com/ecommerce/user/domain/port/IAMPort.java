package com.ecommerce.user.domain.port;

import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import com.ecommerce.user.domain.model.authentication.AccessToken;

/**
 * Identity and Access Management
 */
public interface IAMPort {

    AccessToken authenticate(String username, String password);

    void logout(User user);

    void addUser(User user, Password password);

    void deleteUser(UserId id);

    void deleteUser(Email email);

    void changePassword(User user, Password newPassword);
}
