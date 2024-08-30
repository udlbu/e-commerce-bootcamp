package com.ecommerce.offer;

import com.ecommerce.offer.api.dto.OfferResponse;

import java.util.List;

import static com.ecommerce.shared.Assertions.assertBigDecimalEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {


    public static void assertThatOffersExists(List<OfferResponse> expectedOffers, List<OfferResponse> actualOffers) {
        actualOffers.forEach(actual -> assertThatOfferExists(expectedOffers, actual));
    }

    public static void assertThatOfferExists(List<OfferResponse> expectedOffers, OfferResponse actual) {
        OfferResponse expected = expectedOffers.stream().filter(it -> it.getId().equals(actual.getId())).toList().get(0);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertBigDecimalEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getProduct().getId(), actual.getProduct().getId());
        assertEquals(expected.getProduct().getName(), actual.getProduct().getName());
        assertEquals(expected.getProduct().getEan(), actual.getProduct().getEan());
        assertEquals(expected.getProduct().getImageUrl(), actual.getProduct().getImageUrl());
        assertEquals(expected.getProduct().getDescription(), actual.getProduct().getDescription());
        assertEquals(expected.getProduct().getCategory(), actual.getProduct().getCategory());
        assertEquals(expected.getProduct().getVersion(), actual.getProduct().getVersion());
    }
}
