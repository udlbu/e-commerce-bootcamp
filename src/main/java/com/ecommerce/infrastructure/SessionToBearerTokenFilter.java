package com.ecommerce.infrastructure;

import com.ecommerce.user.domain.model.authentication.Principal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.ecommerce.infrastructure.Constants.X_SESSION_ID;

public class SessionToBearerTokenFilter extends GenericFilterBean {

    private final SessionRegistry sessionRegistry;

    public SessionToBearerTokenFilter(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        ServletRequest customHttpRequest = findSession(httpServletRequest)
                .map(sessionRegistry::getSessionInformation)
                .map(it -> toRequest(it, httpServletRequest)).orElse(request);
        chain.doFilter(customHttpRequest, response);
    }

    private ServletRequest toRequest(SessionInformation sessionInformation, HttpServletRequest httpServletRequest) {
        Principal principal = (Principal) sessionInformation.getPrincipal();
        Assert.isTrue(principal.accessToken() != null, "Token must exist for principal");
        CustomHttpServletRequest customHttpRequest = new CustomHttpServletRequest(httpServletRequest);
        customHttpRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + principal.accessToken().token());
        return customHttpRequest;
    }

    private Optional<String> findSession(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(it -> X_SESSION_ID.equals(it.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }
}