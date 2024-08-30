package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.user.UserFeeder;
import com.ecommerce.user.api.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.ecommerce.IntegrationTestData.DESCRIPTION;
import static com.ecommerce.IntegrationTestData.EAN;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class DeactivateOfferEndpointTest extends BaseIntegrationTest
        implements UserFeeder {

    @Test
    @DisplayName("should deactivate and return deactivated offer")
    public void shouldDeactivateAndReturnDeactivatedOffer() {

        // given
        BigDecimal ADDED_OFFER_PRICE = new BigDecimal("9.99");
        Integer ADDED_OFFER_QUANTITY = 3;
        AddOfferRequest offerRequest = new AddOfferRequest(USER_ID, ADDED_OFFER_PRICE, ADDED_OFFER_QUANTITY, PRODUCT_ID);

        ResponseEntity<OfferResponse> inactiveOfferResponse = offerAbility().addOffer(offerRequest, credentials(EMAIL));
        Long offerId = inactiveOfferResponse.getBody().getId();

        // make sure the offer is inactive
        assertEquals(OfferStatus.INACTIVE.name(), inactiveOfferResponse.getBody().getStatus());

        // then activate offer
        ResponseEntity<OfferResponse> activatedOffer = offerAbility().activateOffer(offerId, credentials(EMAIL));
        assertEquals(OfferStatus.ACTIVE.name(), activatedOffer.getBody().getStatus());
        assertEquals(VERSION + 1, activatedOffer.getBody().getVersion());

        // when
        ResponseEntity<OfferResponse> response = offerAbility().deactivateOffer(offerId, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_OFFER_PRICE, response.getBody().getPrice());
        assertEquals(ADDED_OFFER_QUANTITY, response.getBody().getQuantity());
        assertEquals(USER_ID, response.getBody().getUserId());
        assertEquals(OfferStatus.INACTIVE.name(), response.getBody().getStatus());
        assertEquals(VERSION + 2, response.getBody().getVersion());
        assertEquals(PRODUCT_ID, response.getBody().getProduct().getId());
        assertEquals(NAME, response.getBody().getProduct().getName());
        assertEquals(EAN, response.getBody().getProduct().getEan());
        assertEquals(cdnProperties.getImgUrl() + IMAGE_NAME, response.getBody().getProduct().getImageUrl());
        assertEquals(DESCRIPTION, response.getBody().getProduct().getDescription());
        assertEquals(VERSION, response.getBody().getProduct().getVersion());
    }

    @Test
    @DisplayName("deactivation of inactive offer should not change its state")
    public void deactivationOfInactiveOfferShouldNotChangeItsState() {

        // given
        BigDecimal ADDED_OFFER_PRICE = new BigDecimal("9.99");
        Integer ADDED_OFFER_QUANTITY = 3;
        AddOfferRequest offerRequest = new AddOfferRequest(USER_ID, ADDED_OFFER_PRICE, ADDED_OFFER_QUANTITY, PRODUCT_ID);

        ResponseEntity<OfferResponse> inactiveOfferResponse = offerAbility().addOffer(offerRequest, credentials(EMAIL));
        Long offerId = inactiveOfferResponse.getBody().getId();

        // make sure the offer is inactive
        assertEquals(OfferStatus.INACTIVE.name(), inactiveOfferResponse.getBody().getStatus());
        assertEquals(VERSION, inactiveOfferResponse.getBody().getVersion());

        // when
        ResponseEntity<OfferResponse> response = offerAbility().deactivateOffer(offerId, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(OfferStatus.INACTIVE.name(), response.getBody().getStatus());
        assertEquals(VERSION, response.getBody().getVersion());
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when trying to deactivate non existing offer")
    public void shouldReturnEmptyResponseTryingToDeactivateNonExistingOffer() {

        // when
        ResponseEntity<OfferResponse> response = offerAbility().deactivateOffer(NON_EXISTING_ID, credentials(EMAIL));

        // then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = offerAbility().deactivateOfferUnauthorized(OFFER_ID, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to deactivate not its own offer")
    public void shouldReturnWhenUserIsTryingToDeactivateNotItsOwnOffer() {

        // given
        UserResponse someUserNotOfferOwner = addExampleUser();

        // when
        ResponseEntity<OfferResponse> response = offerAbility().deactivateOffer(OFFER_ID, credentials(someUserNotOfferOwner.getEmail()));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }
}
