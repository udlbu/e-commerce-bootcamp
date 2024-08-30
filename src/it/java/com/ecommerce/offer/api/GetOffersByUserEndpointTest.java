package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.api.dto.OffersPageResponse;
import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.UserFeeder;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.offer.Assertions.assertThatOffersExists;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetOffersByUserEndpointTest extends BaseIntegrationTest implements UserFeeder {

    private final List<OfferResponse> offers = new ArrayList<>();
    private final static int TOTAL_USER_1_OFFERS_NUMBER = 3;
    private final static int PAGE_SIZE = 2;
    private static Long USER_1_ID;
    private static final String USER_1_EMAIL = "name-1@mail.com";
    private static final Predicate<OfferResponse> USER_1_OFFERS = offer -> offer.getUserId().equals(USER_1_ID);

    @BeforeEach
    public void setup() {
        ResponseEntity<UserResponse> user1 = userAbility().addUser(new AddUserRequest(
                USER_1_EMAIL,
                PASSWORD,
                "name-1",
                "surname-1",
                null,
                null
        ));
        USER_1_ID = user1.getBody().getId();
        ResponseEntity<UserResponse> user2 = userAbility().addUser(new AddUserRequest(
                "name-2@mail.com",
                PASSWORD,
                "name-2",
                "surname-2",
                null,
                null
        ));
        ResponseEntity<ProductResponse> product1 = productAbility().addProduct(new AddProductRequest(
                "product-name-1",
                "product-ean-1",
                "BEAUTY",
                null,
                null
        ), credentials(EMAIL));
        ResponseEntity<ProductResponse> product2 = productAbility().addProduct(new AddProductRequest(
                "product-name-2",
                "product-ean-2",
                "BOOKS",
                null,
                null
        ), credentials(EMAIL));
        ResponseEntity<ProductResponse> product3 = productAbility().addProduct(new AddProductRequest(
                "product-name-3",
                "product-ean-3",
                "BOOKS",
                null,
                null
        ), credentials(EMAIL));
        ResponseEntity<ProductResponse> product4 = productAbility().addProduct(new AddProductRequest(
                "product-name-4",
                "product-ean-4",
                "BOOKS",
                null,
                null
        ), credentials(EMAIL));
        ResponseEntity<OfferResponse> offer1 = offerAbility().addOffer(new AddOfferRequest(
                user1.getBody().getId(),
                BigDecimal.valueOf(10),
                3,
                product1.getBody().getId()
        ), credentials(USER_1_EMAIL));
        ResponseEntity<OfferResponse> activatedOffer1 = offerAbility().activateOffer(offer1.getBody().getId(),
                credentials(user1.getBody().getEmail()));
        offers.add(activatedOffer1.getBody());
        ResponseEntity<OfferResponse> offer2 = offerAbility().addOffer(new AddOfferRequest(
                user2.getBody().getId(),
                BigDecimal.valueOf(19),
                7,
                product2.getBody().getId()
        ), credentials(user2.getBody().getEmail()));
        ResponseEntity<OfferResponse> activatedOffer2 = offerAbility().activateOffer(offer2.getBody().getId(),
                credentials(user2.getBody().getEmail()));
        offers.add(activatedOffer2.getBody());
        ResponseEntity<OfferResponse> offer3 = offerAbility().addOffer(new AddOfferRequest(
                user1.getBody().getId(),
                BigDecimal.valueOf(100),
                1,
                product3.getBody().getId()
        ), credentials(USER_1_EMAIL));
        offers.add(offer3.getBody());
        ResponseEntity<OfferResponse> offer4 = offerAbility().addOffer(new AddOfferRequest(
                user1.getBody().getId(),
                BigDecimal.valueOf(10),
                3,
                product4.getBody().getId()
        ), credentials(USER_1_EMAIL));
        ResponseEntity<OfferResponse> activatedOffer4 = offerAbility().activateOffer(offer4.getBody().getId(),
                credentials(user1.getBody().getEmail()));
        offers.add(activatedOffer4.getBody());
    }

    @Test
    @DisplayName("should return first and then second page of both active and inactive offers filtered by user")
    public void shouldReturnOffersFilteredByUser() {

        // given
        int FIRST_PAGE = 0;
        int SECOND_PAGE = 1;

        // when
        ResponseEntity<OffersPageResponse> firstPage = offerAbility().getCurrentUserOffers(credentials(USER_1_EMAIL), FIRST_PAGE, PAGE_SIZE);

        // then
        assertTrue(firstPage.getStatusCode().is2xxSuccessful());
        assertTrue(firstPage.hasBody());
        assertEquals(2, firstPage.getBody().getOffers().size());
        assertEquals(TOTAL_USER_1_OFFERS_NUMBER, firstPage.getBody().getTotal());
        assertThatOffersExists(user1Offers(), firstPage.getBody().getOffers());

        // and when
        ResponseEntity<OffersPageResponse> secondPage = offerAbility().getCurrentUserOffers(credentials(USER_1_EMAIL), SECOND_PAGE, PAGE_SIZE);

        // then
        assertTrue(secondPage.getStatusCode().is2xxSuccessful());
        assertTrue(secondPage.hasBody());
        assertEquals(1, secondPage.getBody().getOffers().size());
        assertEquals(TOTAL_USER_1_OFFERS_NUMBER, secondPage.getBody().getTotal());
        assertThatOffersExists(user1Offers(), firstPage.getBody().getOffers());

        assertFalse(overlap(firstPage.getBody().getOffers(), secondPage.getBody().getOffers()));
    }

    @Test
    @DisplayName("should return first page of both active and inactive offers filtered by user when page and pageSize params are absent")
    public void shouldReturnOffersFilteredByUserWhenQueryParamsAreAbsent() {

        // when
        ResponseEntity<OffersPageResponse> firstPage = offerAbility().getCurrentUserOffers(credentials(USER_1_EMAIL));

        // then
        assertTrue(firstPage.getStatusCode().is2xxSuccessful());
        assertTrue(firstPage.hasBody());
        assertEquals(3, firstPage.getBody().getOffers().size());
        assertEquals(TOTAL_USER_1_OFFERS_NUMBER, firstPage.getBody().getTotal());
        assertThatOffersExists(user1Offers(), firstPage.getBody().getOffers());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = offerAbility().getCurrentUserOffersUnauthorized(invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    private List<OfferResponse> user1Offers() {
        return filterOffers(USER_1_OFFERS);
    }

    private List<OfferResponse> filterOffers(Predicate<OfferResponse> predicate) {
        return offers.stream()
                .filter(predicate)
                .toList();
    }

    private boolean overlap(List<OfferResponse> firstPage, List<OfferResponse> secondPage) {
        Set<Long> first = firstPage.stream().map(OfferResponse::getId).collect(Collectors.toSet());
        Set<Long> second = secondPage.stream().map(OfferResponse::getId).collect(Collectors.toSet());
        for (Long id : second) {
            if (first.contains(id)) {
                return true;
            }
        }
        return false;
    }
}
