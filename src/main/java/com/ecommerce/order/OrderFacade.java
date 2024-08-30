package com.ecommerce.order;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartItemQuantityToOffer;
import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderCreator;
import com.ecommerce.order.domain.OrderId;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.order.domain.SearchOrder;
import com.ecommerce.order.domain.SubmitOrderCommand;
import com.ecommerce.order.domain.exceptions.InactiveOfferException;
import com.ecommerce.order.domain.exceptions.OfferNotAvailableOnStockException;
import com.ecommerce.order.domain.port.OrderRepositoryPort;
import com.ecommerce.shared.domain.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

public class OrderFacade {

    private final static Logger LOG = LoggerFactory.getLogger(OrderFacade.class);

    private final OrderRepositoryPort orderRepository;

    private final CartRepositoryPort cartRepository;

    private final OfferRepositoryPort offerRepositoryPort;

    private final OrderCreator orderCreator;

    public OrderFacade(OrderRepositoryPort orderRepository,
                       CartRepositoryPort cartRepository,
                       OfferRepositoryPort offerRepositoryPort,
                       OrderCreator orderCreator) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.offerRepositoryPort = offerRepositoryPort;
        this.orderCreator = orderCreator;
    }

    public PageResult<Order> getOrders(SearchOrder searchOrder) {
        return orderRepository.findOrders(searchOrder);
    }

    public Order getOrder(OrderId id) {
        return orderRepository.findOrderById(id);
    }

    public void deleteOrder(OrderId orderId) {
        orderRepository.deleteOrderById(orderId);
    }

    public void cancelOrder(OrderId orderId) {
        orderRepository.changeOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    @Transactional
    public OrderId submitOrder(SubmitOrderCommand submitOrderCommand) {
        Cart cart = cartRepository.findAndLockByCartId(submitOrderCommand.cartId());
        Objects.requireNonNull(cart, "Cart must exist");
        validateOffersStockAvailability(cart);
        validateOffersAreActive(cart);
        OrderId orderId = orderCreator.create(submitOrderCommand, cart);
        offerRepositoryPort.changeOffersQuantity(cart);
        cartRepository.clearCartItems(submitOrderCommand.cartId());
        return orderId;
    }

    private void validateOffersAreActive(Cart cart) {
        boolean active = cart.items().stream().allMatch(it -> it.offer().isActive());
        if (!active) {
            LOG.info("All offers must be ACTIVE to create order");
            throw new InactiveOfferException("Some offers become inactive and are not sellable");
        }
    }

    private void validateOffersStockAvailability(Cart cart) {
        cart.toItemQuantityToOffer().forEach(this::validateOfferAvailability);
    }

    private void validateOfferAvailability(CartItemQuantityToOffer item) {
        if (item.offer().quantity().val() - item.cartQuantity().val() < 0) {
            LOG.info(String.format("Offer %s, not available on stock. Ordered: %s, but available: %s",
                    item.offer().id(),
                    item.cartQuantity().val(),
                    item.offer().quantity().val()));
            throw new OfferNotAvailableOnStockException("Not enough offer items available on stock");
        }
    }
}
