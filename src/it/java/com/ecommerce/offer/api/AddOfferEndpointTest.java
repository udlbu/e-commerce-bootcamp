package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.shared.api.dto.Error;
import com.ecommerce.shared.api.dto.Errors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static com.ecommerce.IntegrationTestData.DESCRIPTION;
import static com.ecommerce.IntegrationTestData.EAN;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.IntegrationTestData.OFFER_PRICE;
import static com.ecommerce.IntegrationTestData.OFFER_QUANTITY;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static com.ecommerce.shared.api.dto.ErrorCode.FIELD_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class AddOfferEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should add new offer")
    public void shouldAddNewOffer() {

        // given
        BigDecimal ADDED_OFFER_PRICE = new BigDecimal("9.99");
        Integer ADDED_OFFER_QUANTITY = 3;
        AddOfferRequest offerRequest = new AddOfferRequest(USER_ID, ADDED_OFFER_PRICE, ADDED_OFFER_QUANTITY, PRODUCT_ID);
        // when
        ResponseEntity<OfferResponse> response = offerAbility().addOffer(offerRequest, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_OFFER_PRICE, response.getBody().getPrice());
        assertEquals(ADDED_OFFER_QUANTITY, response.getBody().getQuantity());
        assertEquals(USER_ID, response.getBody().getUserId());
        assertEquals(OfferStatus.INACTIVE.name(), response.getBody().getStatus());
        assertEquals(VERSION, response.getBody().getVersion());
        assertEquals(PRODUCT_ID, response.getBody().getProduct().getId());
        assertEquals(NAME, response.getBody().getProduct().getName());
        assertEquals(EAN, response.getBody().getProduct().getEan());
        assertEquals(cdnProperties.getImgUrl() + IMAGE_NAME, response.getBody().getProduct().getImageUrl());
        assertEquals(DESCRIPTION, response.getBody().getProduct().getDescription());
        assertEquals(VERSION, response.getBody().getProduct().getVersion());
    }


    @Test
    @DisplayName("should return BAD_REQUEST error code when request does not contain required data")
    public void shouldReturnBadRequestWhenRequestDoesNotContainRequiredData() {

        // given
        AddOfferRequest offerRequest = new AddOfferRequest(null, null, null, null);

        // when
        ResponseEntity<Errors> response = offerAbility().addOfferError(offerRequest, credentials(EMAIL));

        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(4, response.getBody().getErrors().size());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(0).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(1).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(2).getCode());
        assertEquals(FIELD_REQUIRED, response.getBody().getErrors().get(3).getCode());
        List<String> missingFields = response.getBody().getErrors().stream().map(Error::getMessage).toList();
        assertTrue(missingFields.contains("userId"));
        assertTrue(missingFields.contains("productId"));
        assertTrue(missingFields.contains("price"));
        assertTrue(missingFields.contains("quantity"));
        assertFalse(response.getBody().getErrors().get(0).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(1).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(2).getPath().isEmpty());
        assertFalse(response.getBody().getErrors().get(3).getPath().isEmpty());
    }

    @Test
    @DisplayName("should return SERVER_ERROR when product does not exist")
    public void shouldReturnServerErrorWhenProductDoesNotExist() {

        // given
        Long NON_EXISTING_PRODUCT = -7L;
        BigDecimal ADDED_OFFER_PRICE = new BigDecimal("9.99");
        Integer ADDED_OFFER_QUANTITY = 3;
        AddOfferRequest offerRequest = new AddOfferRequest(USER_ID, ADDED_OFFER_PRICE, ADDED_OFFER_QUANTITY, NON_EXISTING_PRODUCT

        );

        // when
        ResponseEntity<OfferResponse> response = offerAbility().addOffer(offerRequest, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is5xxServerError());
    }

    @Test
    @DisplayName("should return FORBIDDEN error code when user is trying to add offer on behalf of another user")
    public void shouldReturnForbiddenErrorCodeWhenUserIsTryingToAddOfferOnBehalfOfAnotherUser() {

        // given
        Long SOME_USER_ID = -5L;
        AddOfferRequest offerRequest = new AddOfferRequest(SOME_USER_ID, OFFER_PRICE, OFFER_QUANTITY, PRODUCT_ID);

        // when
        ResponseEntity<OfferResponse> response = offerAbility().addOffer(offerRequest, credentials(EMAIL));

        // then
        assertEquals(FORBIDDEN, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // given
        AddOfferRequest offerRequest = new AddOfferRequest(USER_ID, OFFER_PRICE, OFFER_QUANTITY, PRODUCT_ID);

        // when
        ResponseEntity<Errors> response = offerAbility().addOfferUnauthorized(offerRequest, invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
