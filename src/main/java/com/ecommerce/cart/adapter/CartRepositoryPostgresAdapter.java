package com.ecommerce.cart.adapter;

import com.ecommerce.cart.domain.exceptions.CartNotFoundException;
import com.ecommerce.cart.domain.exceptions.OfferNotFoundException;
import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartId;
import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.adapter.SpringJpaOfferRepository;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.shared.domain.Quantity;
import com.ecommerce.user.domain.model.UserId;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CartRepositoryPostgresAdapter implements CartRepositoryPort {

    private final SpringJpaCartRepository springJpaCartRepository;

    private final SpringJpaOfferRepository springJpaOfferRepository;

    private final SpringJpaCartItemRepository springJpaCartItemRepository;

    public CartRepositoryPostgresAdapter(SpringJpaCartRepository springJpaCartRepository,
                                         SpringJpaOfferRepository springJpaOfferRepository,
                                         SpringJpaCartItemRepository springJpaCartItemRepository) {
        this.springJpaCartRepository = springJpaCartRepository;
        this.springJpaOfferRepository = springJpaOfferRepository;
        this.springJpaCartItemRepository = springJpaCartItemRepository;
    }

    @Override
    public Cart findByUserId(UserId id) {
        return springJpaCartRepository.findByUserId(id.val())
                .map(CartEntity::toDomain)
                .orElse(null);
    }

    @Override
    public void addItem(OfferId offerId, UserId userId, Quantity quantity) {
        springJpaCartRepository.findByUserId(userId.val())
                .ifPresentOrElse(
                        cart -> addToExistingCart(offerId, cart, quantity),
                        () -> createCartWithItem(offerId, userId, quantity));
    }

    private void createCartWithItem(OfferId offerId, UserId userId, Quantity quantity) {
        CartEntity cart = new CartEntity();
        cart.setUserId(userId.val());
        springJpaOfferRepository.findById(offerId.val())
                .ifPresentOrElse(
                        offer -> cart.addOfferItem(offer, quantity),
                        () -> offerNotFound(offerId));
        springJpaCartRepository.save(cart);

    }

    private void addToExistingCart(OfferId offerId, CartEntity cart, Quantity quantity) {
        if (cart.containsOffer(offerId)) {
            cart.incrementQuantity(offerId, quantity);
        } else {
            springJpaOfferRepository.findById(offerId.val())
                    .ifPresentOrElse(
                            offer -> cart.addOfferItem(offer, quantity),
                            () -> offerNotFound(offerId));
        }
    }

    @Override
    public void removeItem(OfferId offerId, UserId userId) {
        springJpaCartRepository.findByUserId(userId.val()).ifPresentOrElse(
                cart -> {
                    CartItemEntity item = cart.findCartItem(offerId);
                    springJpaCartItemRepository.delete(item);
                },
                () -> cartNotFound(userId)
        );
    }

    @Override
    public void changeCartItemQuantity(OfferId offerId, UserId userId, Quantity quantity) {
        springJpaCartRepository.findByUserId(userId.val()).ifPresentOrElse(
                cart -> cart.changeCartItemQuantity(offerId, quantity),
                () -> cartNotFound(userId)
        );
    }

    @Override
    public void deleteByUserId(UserId userId) {
        springJpaCartRepository.deleteByUserId(userId.val());
    }

    @Override
    public Cart findAndLockByCartId(CartId cartId) {
        CartEntity cartEntity = springJpaCartRepository.findAndLockCartById(cartId.val());
        if (cartEntity == null) {
            cartNotFound(cartId);
        }
        return cartEntity.toDomain();
    }

    @Override
    public void clearCartItems(CartId cartId) {
        CartEntity cartEntity = springJpaCartRepository.findById(cartId.val())
                .orElseThrow(() -> new CartNotFoundException(String.format("User cart not found <%s>", cartId)));
        springJpaCartItemRepository.deleteAll(cartEntity.getItems());
        cartEntity.getItems().forEach(it -> it.setCart(null));
        cartEntity.setItems(null);
    }

    private void offerNotFound(OfferId offerId) {
        throw new OfferNotFoundException(String.format("Offer not found <%s>", offerId));
    }

    private <T> void cartNotFound(T id) {
        throw new CartNotFoundException(String.format("User cart not found <%s>", id));
    }
}
