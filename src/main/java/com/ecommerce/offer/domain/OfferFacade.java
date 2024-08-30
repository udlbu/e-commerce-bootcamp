package com.ecommerce.offer.domain;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.offer.domain.model.SearchOffer;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.user.domain.model.UserId;

public class OfferFacade {

    private final OfferRepositoryPort offerRepository;

    public OfferFacade(OfferRepositoryPort offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Offer getOffer(OfferId id) {
        return offerRepository.findOfferById(id);
    }

    public Offer addOffer(Offer offer) {
        return offerRepository.saveOffer(offer);
    }

    public void activateOffer(OfferId offerId) {
        offerRepository.changeOfferStatus(offerId, OfferStatus.ACTIVE);
    }

    public void deactivateOffer(OfferId offerId) {
        offerRepository.changeOfferStatus(offerId, OfferStatus.INACTIVE);
    }

    public void deleteOffer(OfferId offerId) {
        offerRepository.deleteOfferById(offerId);
    }

    public PageResult<Offer> getOffers(SearchOffer searchOffer) {
        return offerRepository.findActiveOffers(searchOffer);
    }

    public PageResult<Offer> getCurrentUserOffers(UserId userId, Integer page, Integer pageSize) {
        return offerRepository.findOffersByUserId(userId, page, pageSize);
    }

    public void modifyOffer(Offer offer) {
        offerRepository.updateOffer(offer);
    }
}
