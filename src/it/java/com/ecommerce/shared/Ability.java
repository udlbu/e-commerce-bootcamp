package com.ecommerce.shared;

import com.ecommerce.cart.CartAbility;
import com.ecommerce.offer.OfferAbility;
import com.ecommerce.order.OrderAbility;
import com.ecommerce.product.ProductAbility;
import com.ecommerce.user.UserAbility;

public interface Ability {

    ProductAbility productAbility();

    UserAbility userAbility();

    CartAbility cartAbility();

    OfferAbility offerAbility();

    OrderAbility orderAbility();
}
