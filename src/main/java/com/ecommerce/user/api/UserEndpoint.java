package com.ecommerce.user.api;

import com.ecommerce.shared.domain.AuthorizationFacade;
import com.ecommerce.user.UserFacade;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.ChangePasswordRequest;
import com.ecommerce.user.api.dto.ModifyUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecommerce.user.api.dto.UserResponse.of;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
public class UserEndpoint {

    private final UserFacade facade;

    private final AuthorizationFacade authorizationFacade;

    public UserEndpoint(UserFacade facade, AuthorizationFacade authorizationFacade) {
        this.facade = facade;
        this.authorizationFacade = authorizationFacade;
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
        authorizationFacade.authorize(new UserId(id));
        User user = facade.getUser(new UserId(id));
        if (user == null) {
            return notFound().build();
        }
        return ok(of(user));
    }

    @GetMapping(value = "/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ok(of(authorizationFacade.getCurrentUser()));
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody AddUserRequest userRequest) {
        User user = facade.addUser(userRequest.toDomain(), userRequest.toPassword());
        return ok(of(user));
    }

    @DeleteMapping(value = "/users/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        authorizationFacade.authorize(new UserId(id));
        facade.deleteUser(new UserId(id));
    }

    @PutMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> modifyUser(@Valid @RequestBody ModifyUserRequest userRequest) {
        authorizationFacade.authorize(new UserId(userRequest.getId()));
        facade.modifyUser(userRequest.toDomain());
        User user = facade.getUser(new UserId(userRequest.getId()));
        if (user == null) {
            return notFound().build();
        }
        return ok(of(user));
    }

    @PutMapping(value = "/users/{id}/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changePassword(@PathVariable("id") Long id, @Valid @RequestBody ChangePasswordRequest request) {
        authorizationFacade.authorize(new UserId(id));
        facade.changePassword(new UserId(id), new Password(request.getOldPassword()), new Password(request.getNewPassword()));
    }
}
