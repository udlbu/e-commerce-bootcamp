package com.ecommerce.offer.adapter;

import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.model.OfferPrice;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.product.adapter.ProductEntity;
import com.ecommerce.user.domain.model.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "OFFERS")
@Data
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "STATUS", length = 20, nullable = false)
    private String status;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToOne
    @JoinColumn(name = "PRODUCT_ID")
    private ProductEntity product;

    public static OfferEntity newOffer(Offer offer, ProductEntity product) {
        OfferEntity offerEntity = new OfferEntity();
        offerEntity.setPrice(offer.price().val());
        offerEntity.setProduct(product);
        offerEntity.setQuantity(offer.quantity().val());
        offerEntity.setStatus(offer.status().name());
        offerEntity.setUserId(offer.userId().val());
        offerEntity.setVersion(offerEntity.getVersion());
        return offerEntity;
    }

    public Offer toDomain() {
        return new Offer(
                new OfferId(id),
                new UserId(userId),
                OfferStatus.valueOf(status),
                new OfferPrice(price),
                new Quantity(quantity),
                new com.ecommerce.shared.domain.Version(version),
                product.toDomain()
        );
    }

    public OfferEntity changeStatus(OfferStatus status) {
        this.status = status.name();
        return this;
    }

    public OfferEntity merge(Offer offer) {
        if (offer.price() != null) {
            setPrice(offer.price().val());
        }
        if (offer.quantity() != null) {
            setQuantity(offer.quantity().val());
        }
        return this;
    }

    public OfferEntity changeProduct(ProductEntity product) {
        setProduct(product);
        return this;
    }
}
