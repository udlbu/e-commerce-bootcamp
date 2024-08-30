package com.ecommerce.offer.domain.port;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.offer.domain.model.SearchOffer;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.user.domain.model.UserId;

public interface OfferRepositoryPort {

    Offer findOfferById(OfferId id);

    Offer saveOffer(Offer offer);

    void changeOfferStatus(OfferId offerId, OfferStatus status);

    void deleteOfferById(OfferId offerId);

    PageResult<Offer> findActiveOffers(SearchOffer request);

    PageResult<Offer> findOffersByUserId(UserId userId, Integer page, Integer pageSize);

    void updateOffer(Offer offer);

    void changeOffersQuantity(Cart cart);
}
