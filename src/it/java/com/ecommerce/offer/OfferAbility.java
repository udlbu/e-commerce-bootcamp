package com.ecommerce.offer;

import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.ModifyOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.api.dto.OffersPageResponse;
import com.ecommerce.offer.api.dto.SearchOffersRequest;
import com.ecommerce.shared.HttpAbility;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.shared.domain.Credentials;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public class OfferAbility extends HttpAbility {

    private final String URL = "/api/offers";

    private final JdbcTemplate jdbcTemplate;

    public OfferAbility(TestRestTemplate testRestTemplate, String authServerUrl, JdbcTemplate jdbcTemplate) {
        super(testRestTemplate, authServerUrl);
        this.jdbcTemplate = jdbcTemplate;
    }


    public ResponseEntity<OfferResponse> getOffer(Long id) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(acceptUnauthorized()), OfferResponse.class);
    }

    public ResponseEntity<OfferResponse> addOffer(AddOfferRequest offerRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(offerRequest, acceptAndContentType(credentials)), OfferResponse.class);
    }

    public ResponseEntity<Errors> addOfferUnauthorized(AddOfferRequest offerRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(offerRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Errors> addOfferError(AddOfferRequest offerRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(offerRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<OfferResponse> activateOffer(Long offerId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + offerId + "/activate", HttpMethod.PUT, new HttpEntity<>(accept(credentials)), OfferResponse.class);
    }

    public ResponseEntity<Errors> activateOfferError(Long offerId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + offerId + "/activate", HttpMethod.PUT, new HttpEntity<>(accept(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> activateOfferUnauthorized(Long offerId, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + offerId + "/activate", HttpMethod.PUT, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<OfferResponse> deactivateOffer(Long offerId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + offerId + "/deactivate", HttpMethod.PUT, new HttpEntity<>(accept(credentials)), OfferResponse.class);
    }

    public ResponseEntity<Errors> deactivateOfferUnauthorized(Long offerId, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + offerId + "/deactivate", HttpMethod.PUT, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Void> deleteOffer(Long offerId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + offerId, HttpMethod.DELETE, new HttpEntity<>(authorization(credentials)), Void.class);
    }

    public ResponseEntity<Errors> deleteOfferError(Long offerId, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + offerId, HttpMethod.DELETE, new HttpEntity<>(authorization(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> deleteOfferUnauthorized(Long offerId, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + offerId, HttpMethod.DELETE, new HttpEntity<>(invalidOrMissingToken(invalidToken)), Errors.class);
    }

    public ResponseEntity<OffersPageResponse> getActiveOffers(SearchOffersRequest searchOffersRequest) {
        return testRestTemplate.exchange(URL + "/search", HttpMethod.POST, new HttpEntity<>(searchOffersRequest, acceptAndContentTypeUnauthorized()), OffersPageResponse.class);
    }

    public ResponseEntity<OffersPageResponse> getCurrentUserOffers(Credentials credentials, int page, int pageSize) {
        return testRestTemplate.exchange(URL + "/user/search?page=" + page + "&pageSize=" + pageSize, HttpMethod.GET, new HttpEntity<>(accept(credentials)), OffersPageResponse.class);
    }

    public ResponseEntity<OffersPageResponse> getCurrentUserOffers(Credentials credentials) {
        return testRestTemplate.exchange(URL + "/user/search", HttpMethod.GET, new HttpEntity<>(accept(credentials)), OffersPageResponse.class);
    }

    public ResponseEntity<Errors> getCurrentUserOffersUnauthorized(String invalidToken) {
        return testRestTemplate.exchange(URL + "/user/search", HttpMethod.GET, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<OfferResponse> modifyOffer(ModifyOfferRequest modifyOfferRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(modifyOfferRequest, acceptAndContentType(credentials)), OfferResponse.class);
    }

    public ResponseEntity<Errors> modifyOfferError(ModifyOfferRequest modifyOfferRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(modifyOfferRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> modifyOfferUnauthorized(ModifyOfferRequest modifyOfferRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(modifyOfferRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public void forceRunOutOfStock(Long offerId) {
        String updateQuery = "UPDATE app.OFFERS SET QUANTITY = 0 WHERE ID = ?";
        jdbcTemplate.update(updateQuery, offerId);
    }
}
