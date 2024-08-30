package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.IntegrationTestData;
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
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.offer.Assertions.assertThatOfferExists;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetAllOffersEndpointTest extends BaseIntegrationTest {

    private final List<OfferResponse> offers = new ArrayList<>();

    // two added in setup + first initial offer from flyway script
    private final static Long TOTAL_NUMBER_OF_ACTIVE_OFFERS_IN_DB = 3L;

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
        ResponseEntity<UserResponse> user3 = userAbility().addUser(new AddUserRequest(
                "name-3@mail.com",
                PASSWORD,
                "name-3",
                "surname-3",
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

        // inactive offer
        offerAbility().addOffer(new AddOfferRequest(
                user3.getBody().getId(),
                BigDecimal.valueOf(12),
                1,
                product3.getBody().getId()
        ), credentials(user3.getBody().getEmail()));
    }

    @Test
    @DisplayName("should return first page of all active offers")
    public void shouldReturnFirstPageOfAllTheActiveOffers() {
        // when
        ResponseEntity<OffersPageResponse> response = offerAbility().getActiveOffers(new SearchOffersRequest(
                null,
                0,
                10
        ));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_IN_DB, response.getBody().getTotal());
        // return all three because page size is 10
        assertEquals(3, response.getBody().getOffers().size());
        assertThatAllOffersExist(response.getBody().getOffers());
    }

    @Test
    @DisplayName("should return pages of all active offers when page size is less than total offers count")
    public void shouldReturnFirstPageOfAllActiveOffersWhenPageSizeIsLessThanTotalOffersCount() {

        // given
        int FIRST_PAGE = 0;
        int SECOND_PAGE = 1;
        int THIRD_PAGE = 2;
        int PAGE_SIZE = 1;

        // when
        ResponseEntity<OffersPageResponse> firstPage = offerAbility().getActiveOffers(new SearchOffersRequest(
                null,
                FIRST_PAGE,
                PAGE_SIZE
        ));

        // then
        assertTrue(firstPage.getStatusCode().is2xxSuccessful());
        assertTrue(firstPage.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_IN_DB, firstPage.getBody().getTotal());
        assertEquals(1, firstPage.getBody().getOffers().size());
        assertThatAllOffersExist(firstPage.getBody().getOffers());

        // and when request for second page
        ResponseEntity<OffersPageResponse> secondPage = offerAbility().getActiveOffers(new SearchOffersRequest(
                null,
                SECOND_PAGE,
                PAGE_SIZE
        ));

        // then
        assertTrue(secondPage.getStatusCode().is2xxSuccessful());
        assertTrue(secondPage.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_IN_DB, secondPage.getBody().getTotal());
        assertEquals(1, secondPage.getBody().getOffers().size());
        assertThatAllOffersExist(secondPage.getBody().getOffers());

        // and when request for third page
        ResponseEntity<OffersPageResponse> thirdPage = offerAbility().getActiveOffers(new SearchOffersRequest(
                null,
                THIRD_PAGE,
                PAGE_SIZE
        ));

        // then
        assertTrue(thirdPage.getStatusCode().is2xxSuccessful());
        assertTrue(thirdPage.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_IN_DB, thirdPage.getBody().getTotal());
        assertEquals(1, thirdPage.getBody().getOffers().size());
        assertThatAllOffersExist(thirdPage.getBody().getOffers());
    }

    @Test
    @DisplayName("should return empty page when there is no more offer")
    public void shouldReturnEmptyPageWhenThereIsNoMoreOffers() {

        // given
        int SECOND_PAGE = 1;
        int PAGE_SIZE = 10;

        // when
        ResponseEntity<OffersPageResponse> secondPage = offerAbility().getActiveOffers(new SearchOffersRequest(
                null,
                SECOND_PAGE,
                PAGE_SIZE
        ));

        // then
        assertTrue(secondPage.getStatusCode().is2xxSuccessful());
        assertTrue(secondPage.hasBody());
        assertEquals(TOTAL_NUMBER_OF_ACTIVE_OFFERS_IN_DB, secondPage.getBody().getTotal());
        // return empty because all three offers are on first page
        assertTrue(secondPage.getBody().getOffers().isEmpty());
    }

    private void assertThatAllOffersExist(List<OfferResponse> result) {
        OfferResponse initialOffer = offerAbility().getOffer(IntegrationTestData.OFFER_ID).getBody();
        List<OfferResponse> expected = new ArrayList<>();
        expected.add(initialOffer);
        expected.addAll(offers);
        result.forEach(it -> assertThatOfferExists(expected, it));
    }
}
