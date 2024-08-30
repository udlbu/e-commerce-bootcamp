package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.api.dto.OffersPageResponse;
import com.ecommerce.offer.api.dto.SearchOffersRequest;
import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.ecommerce.IntegrationTestData.CATEGORY_BOOKS;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.offer.Assertions.assertThatOffersExists;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetOffersByCategoryEndpointTest extends BaseIntegrationTest {

    private final List<OfferResponse> offers = new ArrayList<>();

    private final static int TOTAL_NUMBER_OF_ACTIVE_OFFERS_WITH_PRODUCT_CATEGORY_BOOKS = 2;

    private static final Predicate<OfferResponse> BOOK_CATEGORY_OFFERS = offer -> offer.getProduct().getCategory().equals(CATEGORY_BOOKS);

    @BeforeEach
    public void setup() {
        ResponseEntity<UserResponse> user1 = userAbility().addUser(new AddUserRequest(
                "name-1@mail.com",
                PASSWORD,
                "name-1",
                "surname-1",
                null,
                null
        ));
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
        ResponseEntity<OfferResponse> offer1 = offerAbility().addOffer(new AddOfferRequest(
                user1.getBody().getId(),
                BigDecimal.valueOf(10),
                3,
                product1.getBody().getId()
        ), credentials(user1.getBody().getEmail()));
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
        ), credentials(user1.getBody().getEmail()));
        ResponseEntity<OfferResponse> activatedOffer3 = offerAbility().activateOffer(offer3.getBody().getId(),
                credentials(user1.getBody().getEmail()));
        offers.add(activatedOffer3.getBody());
    }

    @Test
    @DisplayName("should return active offers filtered by category")
    public void shouldReturnActiveOffersFilteredByCategory() {

        // given
        int FIRST_PAGE = 0;
        int PAGE_SIZE = 10;

        // when
        ResponseEntity<OffersPageResponse> response = offerAbility().getActiveOffers(new SearchOffersRequest(
                CATEGORY_BOOKS,
                FIRST_PAGE,
                PAGE_SIZE
        ));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_WITH_PRODUCT_CATEGORY_BOOKS, response.getBody().getTotal());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_WITH_PRODUCT_CATEGORY_BOOKS, response.getBody().getOffers().size());
        assertThatOffersExists(bookOffers(), response.getBody().getOffers());
    }

    @DisplayName("should return page of active offers filtered by category")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void shouldReturnPageOfActiveOffersFilteredByCategory(int pageNumber) {

        // given
        int PAGE_SIZE = 1;

        // when
        ResponseEntity<OffersPageResponse> response = offerAbility().getActiveOffers(new SearchOffersRequest(
                CATEGORY_BOOKS,
                pageNumber,
                PAGE_SIZE
        ));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_WITH_PRODUCT_CATEGORY_BOOKS, response.getBody().getTotal());
        // we have only 2 offers therefore on page number 3 (counting from 0) we have empty offers list
        if (pageNumber == 2) {
            assertTrue(response.getBody().getOffers().isEmpty());
        } else {
            assertEquals(PAGE_SIZE, response.getBody().getOffers().size());
            assertThatOffersExists(bookOffers(), response.getBody().getOffers());
        }
    }

    private List<OfferResponse> bookOffers() {
        return offers.stream()
                .filter(BOOK_CATEGORY_OFFERS)
                .toList();
    }
}
