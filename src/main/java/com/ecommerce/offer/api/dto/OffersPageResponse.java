package com.ecommerce.offer.api.dto;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.shared.domain.PageResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OffersPageResponse {

    private List<OfferResponse> offers;

    private Long total;

    public static OffersPageResponse of(PageResult<Offer> offersPage, String cdnUrl) {
        return new OffersPageResponse(
                offersPage.data().stream().map(offer -> OfferResponse.of(offer, cdnUrl)).toList(),
                offersPage.totalSize().val()
        );
    }
}
