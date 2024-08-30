package com.ecommerce.cart.adapter;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartId;
import com.ecommerce.offer.adapter.OfferEntity;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.user.domain.model.UserId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CARTS")
@Data
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private Long userId;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private Set<CartItemEntity> items;

    public boolean containsOffer(OfferId offerId) {
        if (items == null) {
            return false;
        }
        return items.stream()
                .anyMatch(item -> item.getOffer().getId().equals(offerId.val()));
    }

    public void incrementQuantity(OfferId offerId, Quantity quantity) {
        items.stream()
                .filter(item -> offerId.val().equals(item.getOffer().getId()))
                .findFirst()
                .ifPresent(item -> item.incrementQuantity(quantity));
    }

    public void addOfferItem(OfferEntity offer, Quantity quantity) {
        if (items == null) {
            items = new HashSet<>();
        }
        CartItemEntity item = new CartItemEntity();
        item.setOffer(offer);
        item.setQuantity(quantity.val());
        item.setCart(this);
        items.add(item);
    }

    public Cart toDomain() {
        return new Cart(
                new CartId(id),
                new UserId(userId),
                items != null ? items.stream().map(CartItemEntity::toDomain).toList() : Collections.emptyList(),
                new com.ecommerce.shared.domain.Version(version)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartEntity that = (CartEntity) o;
        return id.equals(that.id) && userId.equals(that.userId) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, version);
    }

    public void changeCartItemQuantity(OfferId offerId, Quantity quantity) {
        if (items == null) {
            return;
        }
        for (CartItemEntity item : items) {
            if (offerId.val().equals(item.getOffer().getId())) {
                item.setQuantity(quantity.val());
            }
        }
    }

    public CartItemEntity findCartItem(OfferId offerId) {
        if (items == null) {
            return null;
        }
        Iterator<CartItemEntity> it = items.iterator();
        while (it.hasNext()) {
            CartItemEntity item = it.next();
            if (offerId.val().equals(item.getOffer().getId())) {
                it.remove();
                item.setCart(null);
                return item;
            }
        }
        return null;
    }
}
