package com.ecommerce.cart.adapter;

import com.ecommerce.cart.domain.model.CartItem;
import com.ecommerce.cart.domain.model.CartItemId;
import com.ecommerce.offer.adapter.OfferEntity;
import com.ecommerce.shared.domain.Quantity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "CART_ITEMS")
@Data
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToOne
    @JoinColumn(name = "OFFER_ID", nullable = false)
    private OfferEntity offer;

    @ManyToOne
    @JoinColumn(name = "CART_ID", nullable = false)
    private CartEntity cart;

    public void incrementQuantity(Quantity incrementBy) {
        quantity += incrementBy.val();
    }

    public CartItem toDomain() {
        return new CartItem(
                new CartItemId(id),
                offer.toDomain(),
                new Quantity(quantity),
                new com.ecommerce.shared.domain.Version(version)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemEntity that = (CartItemEntity) o;
        return id.equals(that.id) && quantity.equals(that.quantity) && version.equals(that.version) && offer.equals(that.offer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity, version, offer);
    }
}
