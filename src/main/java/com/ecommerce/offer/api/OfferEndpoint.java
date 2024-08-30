package com.ecommerce.offer.api;

import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.ModifyOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.offer.api.dto.OffersPageResponse;
import com.ecommerce.offer.api.dto.SearchOffersRequest;
import com.ecommerce.offer.domain.OfferFacade;
import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.shared.domain.AuthorizationFacade;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ecommerce.offer.api.dto.OfferResponse.of;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
public class OfferEndpoint {

    private final OfferFacade facade;
    private final AuthorizationFacade authorizationFacade;
    private final CdnProperties properties;

    public OfferEndpoint(OfferFacade facade, AuthorizationFacade authorizationFacade, CdnProperties properties) {
        this.facade = facade;
        this.authorizationFacade = authorizationFacade;
        this.properties = properties;
    }

    @GetMapping(value = "/offers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferResponse> getOffer(@PathVariable("id") Long id, HttpServletRequest request) {
        Offer offer = facade.getOffer(new OfferId(id));
        if (offer == null) {
            return notFound().build();
        }
        return ok(of(offer, properties.getImgUrl(request)));
    }

    @PostMapping(value = "/offers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferResponse> addOffer(@Valid @RequestBody AddOfferRequest offerRequest, HttpServletRequest request) {
        authorizationFacade.authorize(new UserId(offerRequest.getUserId()));
        Offer offer = facade.addOffer(offerRequest.toDomain());
        return ok(OfferResponse.of(offer, properties.getImgUrl(request)));
    }

    @PutMapping(value = "/offers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferResponse> modifyOffer(@Valid @RequestBody ModifyOfferRequest offerRequest, HttpServletRequest request) {
        authorizationFacade.authorize(new OfferId(offerRequest.getOfferId()));
        authorizationFacade.authorize(new UserId(offerRequest.getUserId()));
        facade.modifyOffer(offerRequest.toDomain());
        Offer offer = facade.getOffer(new OfferId(offerRequest.getOfferId()));
        if (offer == null) {
            return notFound().build();
        }
        return ok(OfferResponse.of(offer, properties.getImgUrl(request)));
    }

    @PutMapping(value = "/offers/{id}/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferResponse> activateOffer(@PathVariable("id") Long id, HttpServletRequest request) {
        authorizationFacade.authorize(new OfferId(id));
        facade.activateOffer(new OfferId(id));
        Offer offer = facade.getOffer(new OfferId(id));
        if (offer == null) {
            return notFound().build();
        }
        return ok(of(offer, properties.getImgUrl(request)));
    }

    @PutMapping(value = "/offers/{id}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfferResponse> deactivateOffer(@PathVariable("id") Long id, HttpServletRequest request) {
        authorizationFacade.authorize(new OfferId(id));
        facade.deactivateOffer(new OfferId(id));
        Offer offer = facade.getOffer(new OfferId(id));
        if (offer == null) {
            return notFound().build();
        }
        return ok(of(offer, properties.getImgUrl(request)));
    }

    @DeleteMapping(value = "/offers/{id}")
    public void deleteOffer(@PathVariable("id") Long id) {
        authorizationFacade.authorize(new OfferId(id));
        facade.deleteOffer(new OfferId(id));
    }

    @PostMapping(value = "/offers/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OffersPageResponse> getActiveOffers(@Valid @RequestBody SearchOffersRequest searchOffersRequest, HttpServletRequest request) {
        PageResult<Offer> offers = facade.getOffers(searchOffersRequest.toDomain());
        return ok(OffersPageResponse.of(offers, properties.getImgUrl(request)));
    }

    @GetMapping(value = "/offers/user/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OffersPageResponse> getCurrentUserOffers(HttpServletRequest request,
                                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = authorizationFacade.getCurrentUser();
        PageResult<Offer> offers = facade.getCurrentUserOffers(user.id(), page, pageSize);
        return ok(OffersPageResponse.of(offers, properties.getImgUrl(request)));
    }
}
