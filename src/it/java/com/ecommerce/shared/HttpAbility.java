package com.ecommerce.shared;

import com.ecommerce.shared.domain.Credentials;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

public class HttpAbility extends JwtTokenAbility {

    public HttpAbility(TestRestTemplate testRestTemplate, String authServerUrl) {
        super(testRestTemplate, authServerUrl);
    }

    public LinkedMultiValueMap<String, String> accept(Credentials credentials) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt(credentials.username(), credentials.password()));
        return headers;
    }

    public LinkedMultiValueMap<String, String> accept(String sessionId) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.COOKIE, sessionId);
        return headers;
    }

    public LinkedMultiValueMap<String, String> acceptUnauthorized(String invalidToken) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        if (invalidToken != null) {
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken);
        }
        return headers;
    }

    public LinkedMultiValueMap<String, String> acceptUnauthorized() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public LinkedMultiValueMap<String, String> acceptAndContentType(Credentials credentials) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt(credentials.username(), credentials.password()));
        return headers;
    }

    public LinkedMultiValueMap<String, String> acceptAndContentTypeUnauthorized() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public LinkedMultiValueMap<String, String> acceptAndContentTypeUnauthorized(String invalidToken) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (invalidToken != null) {
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken);
        }
        return headers;
    }

    public LinkedMultiValueMap<String, String> sessionCookie(String sessionId) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.COOKIE, sessionId);
        return headers;
    }

    public LinkedMultiValueMap<String, String> authorization(Credentials credentials) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt(credentials.username(), credentials.password()));
        return headers;
    }

    public LinkedMultiValueMap<String, String> invalidOrMissingToken(String invalidToken) {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        if (invalidToken != null) {
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken);
        }
        return headers;
    }
}
