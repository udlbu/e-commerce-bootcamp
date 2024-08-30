package com.ecommerce.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_CLIENT_ID;

public class JwtTokenAbility {

    protected final TestRestTemplate testRestTemplate;
    private final String oauthTokenEndpoint;

    public JwtTokenAbility(TestRestTemplate testRestTemplate, String oauthTokenEndpoint) {
        this.testRestTemplate = testRestTemplate;
        this.oauthTokenEndpoint = oauthTokenEndpoint;
    }

    public void assertUserCanLogIn(String username, String password) {
        jwt(username, password);
    }

    protected String jwt(String username, String password) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList("password"));
        map.put("client_id", Collections.singletonList(KEYCLOAK_CLIENT_ID));
        map.put("username", Collections.singletonList(username));
        map.put("password", Collections.singletonList(password));
        Token token = testRestTemplate.postForObject(
                oauthTokenEndpoint,
                new HttpEntity<>(map, httpHeaders),
                Token.class);
        Assert.assertNotNull(token);
        Assert.assertNotNull(token.accessToken);
        return token.accessToken;
    }

    private record Token(String accessToken) {
        @JsonCreator
        private Token(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
