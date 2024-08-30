package com.ecommerce.order.api;

import com.ecommerce.cart.domain.model.CartId;
import com.ecommerce.order.OrderFacade;
import com.ecommerce.order.api.dto.OrderResponse;
import com.ecommerce.order.api.dto.OrdersPageResponse;
import com.ecommerce.order.api.dto.SearchOrdersRequest;
import com.ecommerce.order.api.dto.SubmitOrderRequest;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderId;
import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.shared.domain.AuthorizationFacade;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.user.domain.model.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ecommerce.order.api.dto.OrderResponse.of;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
public class OrderEndpoint {

    private final OrderFacade facade;

    private final AuthorizationFacade authorizationFacade;

    private final CdnProperties properties;

    public OrderEndpoint(OrderFacade facade, AuthorizationFacade authorizationFacade, CdnProperties properties) {
        this.facade = facade;
        this.authorizationFacade = authorizationFacade;
        this.properties = properties;
    }

    @GetMapping(value = "/orders/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") Long id, HttpServletRequest request) {
        authorizationFacade.authorize(new OrderId(id));
        Order order = facade.getOrder(new OrderId(id));
        if (order == null) {
            return notFound().build();
        }
        return ok(of(order, properties.getImgUrl(request)));
    }

    @PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> submitOrder(@Valid @RequestBody SubmitOrderRequest submitOrderRequest) {
        authorizationFacade.authorize(new CartId(submitOrderRequest.getCartId()));
        OrderId orderId = facade.submitOrder(submitOrderRequest.toDomain());
        return ResponseEntity.ok(orderId.val());
    }

    @DeleteMapping(value = "/orders/{id}")
    public void deleteOrder(@PathVariable("id") Long id) {
        authorizationFacade.authorize(new OrderId(id));
        facade.deleteOrder(new OrderId(id));
    }

    @PutMapping(value = "/orders/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable("id") Long id, HttpServletRequest request) {
        authorizationFacade.authorize(new OrderId(id));
        facade.cancelOrder(new OrderId(id));
        Order order = facade.getOrder(new OrderId(id));
        if (order == null) {
            return notFound().build();
        }
        return ok(of(order, properties.getImgUrl(request)));
    }

    @PostMapping(value = "/orders/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrdersPageResponse> getOrders(@Valid @RequestBody SearchOrdersRequest searchOrdersRequest, HttpServletRequest request) {
        authorizationFacade.authorize(new UserId(searchOrdersRequest.getUserId()));
        PageResult<Order> orders = facade.getOrders(searchOrdersRequest.toDomain());
        return ok(OrdersPageResponse.of(orders, properties.getImgUrl(request)));
    }
}
