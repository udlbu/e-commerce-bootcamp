package com.ecommerce.shared;

import com.ecommerce.user.config.IAMProperties;
import org.keycloak.admin.client.Keycloak;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDataCleaner {

    private final JdbcTemplate jdbcTemplate;

    private final Keycloak keycloakApi;

    private final IAMProperties properties;

    public TestDataCleaner(JdbcTemplate jdbcTemplate, Keycloak keycloakApi, IAMProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.keycloakApi = keycloakApi;
        this.properties = properties;
    }

    public void clean() {
        cleanDatabase();
        cleanKeycloak();
    }

    private void cleanDatabase() {
        // we are not removing data loaded from flyway scripts
        Long userId = jdbcTemplate.queryForObject(
                "SELECT ID FROM app.USERS WHERE EMAIL = '" + EMAIL + "'",
                Long.class);
        jdbcTemplate.update("DELETE FROM app.ORDER_LINES");
        jdbcTemplate.update("DELETE FROM app.ORDERS");
        jdbcTemplate.update("DELETE FROM app.CART_ITEMS");
        jdbcTemplate.update("DELETE FROM app.CARTS");
        jdbcTemplate.update("DELETE FROM app.OFFERS WHERE ID <> ?", OFFER_ID); // first offer is from flyway
        jdbcTemplate.update("DELETE FROM app.PRODUCTS WHERE ID <> ?", PRODUCT_ID); // first product from flyway
        jdbcTemplate.update("DELETE FROM app.ADDRESSES WHERE USER_ID <> ?", userId);
        jdbcTemplate.update("DELETE FROM app.USER_ACTIVATIONS WHERE USER_ID <> ?", userId);
        jdbcTemplate.update("DELETE FROM app.USERS WHERE ID <> ?", userId);

        assertEquals(0, countRecords("ORDER_LINES"));
        assertEquals(0, countRecords("ORDERS"));
        assertEquals(0, countRecords("CART_ITEMS"));
        assertEquals(0, countRecords("CARTS"));
        assertEquals(1, countRecords("OFFERS"));
        assertEquals(1, countRecords("PRODUCTS"));
        assertEquals(1, countRecords("ADDRESSES"));
        assertEquals(1, countRecords("USERS"));
    }

    private void cleanKeycloak() {
        keycloakApi.realm(properties.getRealm())
                .users()
                .list()
                .stream()
                .filter(keycloakUser -> !Objects.equals(keycloakUser.getEmail(), EMAIL))
                .forEach(keycloakUser -> keycloakApi.realm(properties.getRealm())
                        .users().delete(keycloakUser.getId()));
        assertEquals(1, keycloakApi.realm(properties.getRealm())
                .users()
                .list().size());
    }

    private Long countRecords(String tableName) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app." + tableName,
                Long.class);
    }
}
