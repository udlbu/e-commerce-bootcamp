package com.ecommerce.offer.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.offer.api.dto.OfferResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static com.ecommerce.IntegrationTestData.CATEGORY_ELECTRONICS;
import static com.ecommerce.IntegrationTestData.DESCRIPTION;
import static com.ecommerce.IntegrationTestData.EAN;
import static com.ecommerce.IntegrationTestData.IMAGE_NAME;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.IntegrationTestData.NON_EXISTING_ID;
import static com.ecommerce.IntegrationTestData.OFFER_ID;
import static com.ecommerce.IntegrationTestData.OFFER_PRICE;
import static com.ecommerce.IntegrationTestData.OFFER_QUANTITY;
import static com.ecommerce.IntegrationTestData.OFFER_STATUS;
import static com.ecommerce.IntegrationTestData.PRODUCT_ID;
import static com.ecommerce.IntegrationTestData.USER_ID;
import static com.ecommerce.IntegrationTestData.VERSION;
import static com.ecommerce.shared.Assertions.assertBigDecimalEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class GetOfferEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should return offer when offer with given id exists in the system")
    public void shouldReturnOfferWhenOfferWithGivenIdExistsInTheSystem() {

        // when
        ResponseEntity<OfferResponse> response = offerAbility().getOffer(OFFER_ID);

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertEquals(OFFER_ID, response.getBody().getId());
        assertEquals(USER_ID, response.getBody().getUserId());
        assertBigDecimalEquals(OFFER_PRICE, response.getBody().getPrice());
        assertEquals(OFFER_QUANTITY, response.getBody().getQuantity());
        assertEquals(OFFER_STATUS, response.getBody().getStatus());
        assertEquals(PRODUCT_ID, response.getBody().getProduct().getId());
        assertEquals(NAME, response.getBody().getProduct().getName());
        assertEquals(EAN, response.getBody().getProduct().getEan());
        assertEquals(CATEGORY_ELECTRONICS, response.getBody().getProduct().getCategory());
        assertEquals(cdnProperties.getImgUrl() + IMAGE_NAME, response.getBody().getProduct().getImageUrl());
        assertEquals(DESCRIPTION, response.getBody().getProduct().getDescription());
        assertEquals(VERSION, response.getBody().getProduct().getVersion());
    }

    @Test
    @DisplayName("should return NOT_FOUND error code when offer with given id does not exists in the system")
    public void shouldReturnEmptyResponseWhenGetOfferDoesNotExistInTheSystem() {

        // when
        ResponseEntity<OfferResponse> response = offerAbility().getOffer(NON_EXISTING_ID);

        // then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }
}
