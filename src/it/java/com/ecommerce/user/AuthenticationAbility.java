package com.ecommerce.user;

import com.ecommerce.shared.HttpAbility;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.api.dto.CredentialsRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AuthenticationAbility extends HttpAbility {

    public AuthenticationAbility(TestRestTemplate testRestTemplate, String authServerUrl) {
        super(testRestTemplate, authServerUrl);
    }

    public ResponseEntity<Void> authenticate(CredentialsRequest request) {
        return testRestTemplate.exchange("/api/authenticate", HttpMethod.POST, new HttpEntity<>(request, acceptAndContentTypeUnauthorized()), Void.class);
    }

    public ResponseEntity<Errors> authenticateError(CredentialsRequest request) {
        return testRestTemplate.exchange("/api/authenticate", HttpMethod.POST, new HttpEntity<>(request, acceptAndContentTypeUnauthorized()), Errors.class);
    }

    public ResponseEntity<Void> logout(String sessionId) {
        return testRestTemplate.exchange("/api/logout", HttpMethod.POST, new HttpEntity<>(null, accept(sessionId)), Void.class);
    }
}
