package com.ecommerce.offer.api.dto;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.product.api.dto.ProductResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OfferResponse {

    private Long id;
    private Long userId;
    private String status;
    private BigDecimal price;
    private Integer quantity;
    private Long version;
    private ProductResponse product;


    public static OfferResponse of(Offer offer, String cdnUrl) {
        OfferResponse response = new OfferResponse();
        response.setId(offer.id().val());
        response.setUserId(offer.userId().val());
        response.setStatus(offer.status().name());
        response.setPrice(offer.price().val());
        response.setQuantity(offer.quantity().val());
        response.setVersion(offer.version().val());
        response.setProduct(ProductResponse.of(offer.product(), cdnUrl));
        return response;
    }

    public static List<OfferResponse> of(List<Offer> offers, String cdnUrl) {
        return offers.stream().map(offer -> OfferResponse.of(offer, cdnUrl)).toList();
    }
}