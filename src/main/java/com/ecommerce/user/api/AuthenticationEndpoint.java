package com.ecommerce.user.api;

import com.ecommerce.shared.domain.AuthorizationFacade;
import com.ecommerce.user.AuthenticationFacade;
import com.ecommerce.user.api.dto.CredentialsRequest;
import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.authentication.Principal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ecommerce.infrastructure.Constants.X_SESSION_ID;

@RestController
public class AuthenticationEndpoint {

    private final SessionRegistry sessionRegistry;

    private final AuthenticationFacade facade;

    private final AuthorizationFacade authorizationFacade;

    private final SessionIdGenerator sessionIdGenerator;

    public AuthenticationEndpoint(SessionRegistry sessionRegistry,
                                  AuthenticationFacade facade,
                                  AuthorizationFacade authorizationFacade) {
        this.sessionRegistry = sessionRegistry;
        this.facade = facade;
        this.authorizationFacade = authorizationFacade;
        this.sessionIdGenerator = new StandardSessionIdGenerator();
    }

    @PostMapping(value = "/api/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void authenticate(@RequestBody CredentialsRequest credentials, HttpServletResponse response) {
        Principal principal = facade.authenticate(new Email(credentials.getUsername()), new Password(credentials.getPassword()));
        invalidateExistingSessions(principal);
        String sessionId = generateSessionId();
        setCookie(response, sessionId);
        sessionRegistry.registerNewSession(sessionId, principal);
    }

    @PostMapping(value = "/api/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout() {
        User user = authorizationFacade.getCurrentUser();
        facade.logout(user);
        Principal principal = new Principal(user.email().val());
        invalidateExistingSessions(principal);
    }

    private void invalidateExistingSessions(Principal principal) {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, true);
        sessions.forEach(it -> sessionRegistry.removeSessionInformation(it.getSessionId()));
    }

    private void setCookie(HttpServletResponse response, String sessionId) {
        Cookie cookie = new Cookie(X_SESSION_ID, sessionId);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600); // 1 hour
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String generateSessionId() {
        String id;
        do {
            id = sessionIdGenerator.generateSessionId();
        } while (sessionRegistry.getSessionInformation(id) != null);
        return id;
    }
}
