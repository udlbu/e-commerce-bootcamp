package com.ecommerce.cart.domain.model;

import com.ecommerce.offer.domain.model.OfferQuantityChange;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.shared.domain.Version;
import com.ecommerce.user.domain.model.UserId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Cart(CartId id, UserId userId, List<CartItem> items, Version version) {

    public List<CartItemQuantityToOffer> toItemQuantityToOffer() {
        return items()
                .stream()
                .map(item -> new CartItemQuantityToOffer(item.quantity(), item.offer()))
                .toList();
    }

    public Map<Long, OfferQuantityChange> toQuantityChanges() {
        return toMap(toItemQuantityToOffer().stream()
                .map(it -> new OfferQuantityChange(it.offer(), new Quantity(it.offer().quantity().val() - it.cartQuantity().val())))
                .toList());
    }

    private Map<Long, OfferQuantityChange> toMap(List<OfferQuantityChange> changes) {
        Map<Long, OfferQuantityChange> map = new HashMap<>();
        for (OfferQuantityChange change : changes) {
            map.put(change.offer().id().val(), change);
        }
        return map;
    }

    public List<Long> toRawOfferIds() {
        return items.stream().map(it -> it.offer().id().val()).toList();
    }
}