package com.ecommerce.user;

import com.ecommerce.shared.HttpAbility;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserActivationAbility extends HttpAbility {

    private final String URL = "/activate";
    private final JdbcTemplate jdbcTemplate;


    public UserActivationAbility(TestRestTemplate testRestTemplate, String authServerUrl, JdbcTemplate jdbcTemplate) {
        super(testRestTemplate, authServerUrl);
        this.jdbcTemplate = jdbcTemplate;
    }


    public ResponseEntity<String> activateUser(String token) {
        return testRestTemplate.exchange(URL + "?token=" + token, HttpMethod.GET, new HttpEntity<>(null, new LinkedMultiValueMap<>()), String.class);
    }

    public void assertThatUserActivationIsNew(Long userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app.USER_ACTIVATIONS WHERE USER_ID = ? AND STATUS = 'NEW'",
                Integer.class,
                userId);
        assertEquals(1, count);
    }

    public void assertThatUserActivationIsActivated(Long userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app.USER_ACTIVATIONS WHERE USER_ID = ? AND STATUS = 'ACTIVATED'",
                Integer.class,
                userId);
        assertEquals(1, count);
    }

    public String getNewUserToken(Long userId) {
        return jdbcTemplate.queryForObject("SELECT TOKEN FROM app.USER_ACTIVATIONS WHERE USER_ID = ? AND STATUS = 'NEW'",
                String.class,
                userId);
    }
}
