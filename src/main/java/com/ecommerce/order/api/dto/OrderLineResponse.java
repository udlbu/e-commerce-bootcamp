package com.ecommerce.order.api.dto;

import com.ecommerce.order.domain.OrderLine;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecommerce.shared.domain.ImageResolver.getImageName;

@Data
public class OrderLineResponse {

    private Long id;
    private Long offerId;
    private BigDecimal offerPrice;
    private Long productId;
    private String productName;
    private String productEan;
    private String productCategory;
    private String imageUrl;
    private int quantity;
    private Long version;

    public static OrderLineResponse of(OrderLine orderLine, String cdnUrl) {
        OrderLineResponse orderLineResponse = new OrderLineResponse();
        orderLineResponse.setId(orderLine.id().val());
        orderLineResponse.setOfferId(orderLine.offer().id().val());
        orderLineResponse.setOfferPrice(orderLine.offer().price().val());
        orderLineResponse.setProductId(orderLine.offer().product().id().val());
        orderLineResponse.setProductName(orderLine.offer().product().name().val());
        orderLineResponse.setProductEan(orderLine.offer().product().ean().val());
        orderLineResponse.setProductCategory(orderLine.offer().product().category().name());
        orderLineResponse.setImageUrl(cdnUrl + getImageName(cdnUrl, orderLine.offer().product().imageName().val()));
        orderLineResponse.setQuantity(orderLine.quantity().val());
        orderLineResponse.setVersion(orderLine.version().val());
        return orderLineResponse;
    }
}
