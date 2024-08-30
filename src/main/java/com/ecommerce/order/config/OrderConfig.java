package com.ecommerce.order.config;

import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.adapter.SpringJpaOfferRepository;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.order.OrderFacade;
import com.ecommerce.order.adapter.ChangeDeliveryStatusScheduler;
import com.ecommerce.order.adapter.OrderRepositoryPostgresAdapter;
import com.ecommerce.order.adapter.SpringJpaOrderRepository;
import com.ecommerce.order.domain.OrderCreator;
import com.ecommerce.order.domain.port.OrderRepositoryPort;
import com.ecommerce.shared.domain.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

    @Bean
    public OrderFacade orderFacade(OrderRepositoryPort orderRepositoryPort,
                                   CartRepositoryPort cartRepositoryPort,
                                   OfferRepositoryPort offerRepositoryPort,
                                   OrderCreator orderCreator) {
        return new OrderFacade(orderRepositoryPort, cartRepositoryPort, offerRepositoryPort, orderCreator);
    }

    @Bean
    public OrderCreator orderCreator(OrderRepositoryPort orderRepository,
                                     TimeProvider timeProvider) {
        return new OrderCreator(orderRepository, timeProvider);
    }

    @Bean
    OrderRepositoryPort orderRepository(SpringJpaOrderRepository springJpaOrderRepository,
                                        SpringJpaOfferRepository springJpaOfferRepository,
                                        TimeProvider timeProvider) {
        return new OrderRepositoryPostgresAdapter(
                springJpaOrderRepository,
                springJpaOfferRepository,
                timeProvider);
    }

    @Bean
    ChangeDeliveryStatusScheduler changeDeliveryStatusScheduler(OrderRepositoryPort orderRepository) {
        return new ChangeDeliveryStatusScheduler(orderRepository);
    }
}
