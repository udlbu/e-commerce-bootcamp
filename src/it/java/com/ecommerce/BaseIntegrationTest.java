package com.ecommerce;

import com.ecommerce.cart.CartAbility;
import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.infrastructure.Constants;
import com.ecommerce.mail.domain.ActivationEmailSender;
import com.ecommerce.offer.OfferAbility;
import com.ecommerce.order.OrderAbility;
import com.ecommerce.order.OrderFeeder;
import com.ecommerce.order.adapter.SpringJpaOrderLineRepository;
import com.ecommerce.order.adapter.SpringJpaOrderRepository;
import com.ecommerce.product.ProductAbility;
import com.ecommerce.product.ProductFeeder;
import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.shared.TestDataCleaner;
import com.ecommerce.shared.config.InMemoryActivationEmailSender;
import com.ecommerce.shared.config.WireMockConnectionCloseExtension;
import com.ecommerce.user.AuthenticationAbility;
import com.ecommerce.user.UserAbility;
import com.ecommerce.user.UserActivationAbility;
import com.ecommerce.user.domain.port.UserActivationRepositoryPort;
import com.ecommerce.user.domain.port.UserRepositoryPort;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.stream.Stream;

import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_ADMIN_PASSWORD;
import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_ADMIN_USERNAME;
import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_IMAGE;
import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_IMAGE_VERSION;
import static com.ecommerce.shared.config.KeycloakIntegrationConstants.KEYCLOAK_REALM;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"integration"})
public class BaseIntegrationTest implements
        OrderFeeder,
        ProductFeeder {

    private ProductAbility productAbility;

    private UserAbility userAbility;

    private OfferAbility offerAbility;

    private CartAbility cartAbility;

    private OrderAbility orderAbility;

    private CdnAbility cdnAbility;

    private UserActivationAbility userActivationAbility;

    private AuthenticationAbility authenticationAbility;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected CartRepositoryPort cartRepository;

    @Autowired
    protected UserRepositoryPort userRepository;

    @Autowired
    protected UserActivationRepositoryPort userActivationRepository;

    @Autowired
    protected SpringJpaOrderRepository springJpaOrderRepository;

    @Autowired
    protected SpringJpaOrderLineRepository springJpaOrderLineRepository;

    @Autowired
    protected TestDataCleaner testDataCleaner;

    @Autowired
    private ActivationEmailSender inMemoryActivationEmailSender;

    @Autowired
    protected Keycloak keycloakApi;

    @Autowired
    protected CdnProperties cdnProperties;

    public static WireMockClassRule wireMock = new WireMockClassRule(
            wireMockConfig()
                    .dynamicPort()
                    .extensions(new WireMockConnectionCloseExtension())
    );

    protected static KeycloakContainer keycloak = new KeycloakContainer(KEYCLOAK_IMAGE + ":" + KEYCLOAK_IMAGE_VERSION)
            .withRealmImportFile("keycloak/integration-realm-export.json")
            .withAdminUsername(KEYCLOAK_ADMIN_USERNAME)
            .withAdminPassword(KEYCLOAK_ADMIN_PASSWORD)
            .withEnv("DB_VENDOR", "h2");

    static {
        keycloak.start();
        wireMock.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "realms/" + KEYCLOAK_REALM);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloak.getAuthServerUrl() + "realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/certs");

        registry.add("iam.keycloak-server", () -> keycloak.getAuthServerUrl());
        registry.add("iam.realm", () -> KEYCLOAK_REALM);
        registry.add("iam.admin.username", () -> KEYCLOAK_ADMIN_USERNAME);
        registry.add("iam.admin.password", () -> KEYCLOAK_ADMIN_PASSWORD);

        registry.add("cdn.host", () -> wireMock.baseUrl());
    }

    @BeforeEach
    public void setup() {
        cdnAbility().stubUploadImageReturnSuccess();
    }

    @AfterEach
    public void cleanup() {
        testDataCleaner.clean();
        wireMock.resetAll();
        inMemoryActivationEmailSender().mails.clear();
    }

    private String oauthTokenEndpoint() {
        return keycloak.getAuthServerUrl() + "realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token";
    }

    public ProductAbility productAbility() {
        if (productAbility == null) {
            productAbility = new ProductAbility(restTemplate, oauthTokenEndpoint());
        }
        return productAbility;
    }

    public UserAbility userAbility() {
        if (userAbility == null) {
            userAbility = new UserAbility(restTemplate, oauthTokenEndpoint(), jdbcTemplate, keycloakApi);
        }
        return userAbility;
    }

    public OfferAbility offerAbility() {
        if (offerAbility == null) {
            offerAbility = new OfferAbility(restTemplate, oauthTokenEndpoint(), jdbcTemplate);
        }
        return offerAbility;
    }

    public CartAbility cartAbility() {
        if (cartAbility == null) {
            cartAbility = new CartAbility(restTemplate, oauthTokenEndpoint());
        }
        return cartAbility;
    }

    public OrderAbility orderAbility() {
        if (orderAbility == null) {
            orderAbility = new OrderAbility(restTemplate, oauthTokenEndpoint());
        }
        return orderAbility;
    }

    protected AuthenticationAbility authenticationAbility() {
        if (authenticationAbility == null) {
            authenticationAbility = new AuthenticationAbility(restTemplate, oauthTokenEndpoint());
        }
        return authenticationAbility;
    }

    protected CdnAbility cdnAbility() {
        if (cdnAbility == null) {
            cdnAbility = new CdnAbility(cdnProperties, wireMock);
        }
        return cdnAbility;
    }

    protected UserActivationAbility userActivationAbility() {
        if (userActivationAbility == null) {
            userActivationAbility = new UserActivationAbility(restTemplate, oauthTokenEndpoint(), jdbcTemplate);
        }
        return userActivationAbility;
    }

    protected String sessionCookie(ResponseEntity<Void> authenticateResponse) {
        return Stream.ofNullable(authenticateResponse.getHeaders().get("Set-Cookie"))
                .flatMap(Collection::stream)
                .filter(it -> it.startsWith(Constants.X_SESSION_ID))
                .map(it -> it.split(";")[0])
                .findFirst().orElse(null);
    }

    protected InMemoryActivationEmailSender inMemoryActivationEmailSender(){
        return ((InMemoryActivationEmailSender) inMemoryActivationEmailSender);
    }
}
