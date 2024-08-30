package com.ecommerce.shared.domain;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartId;
import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderId;
import com.ecommerce.order.domain.port.OrderRepositoryPort;
import com.ecommerce.shared.domain.exception.UnauthorizedAccessException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AuthorizationFacade {

    private final OfferRepositoryPort offerRepository;

    private final OrderRepositoryPort orderRepository;

    private final CartRepositoryPort cartRepository;

    public AuthorizationFacade(OfferRepositoryPort offerRepository,
                               OrderRepositoryPort orderRepository,
                               CartRepositoryPort cartRepository) {
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public void authorize(UserId userId) {
        validateOwnership(userId);
    }

    public void authorize(OfferId offerId) {
        Offer offer = offerRepository.findOfferById(offerId);
        if (offer == null) {
            return;
        }
        validateOwnership(offer.userId());
    }

    public void authorize(OrderId orderId) {
        Order order = orderRepository.findOrderById(orderId);
        if (order == null) {
            return;
        }
        validateOwnership(order.buyerId());
    }

    public void authorize(CartId cartId) {
        Cart cart = cartRepository.findAndLockByCartId(cartId);
        if (cart == null) {
            return;
        }
        validateOwnership(cart.userId());
    }

    public User getCurrentUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (user == null) {
            throw new UnauthorizedAccessException("Only resource owner can execute this action");
        }
        return user;
    }

    private void validateOwnership(UserId userId) {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (!Objects.equals(user.id().val(), userId.val())) {
            throw new UnauthorizedAccessException("Only resource owner can execute this action");
        }
    }
}
