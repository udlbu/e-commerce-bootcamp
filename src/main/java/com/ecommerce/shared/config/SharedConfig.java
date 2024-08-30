package com.ecommerce.shared.config;

import com.ecommerce.cart.domain.port.CartRepositoryPort;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.order.domain.port.OrderRepositoryPort;
import com.ecommerce.shared.domain.AuthorizationFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedConfig {

    @Bean
    public AuthorizationFacade authorizationFacade(OfferRepositoryPort offerRepository,
                                                   OrderRepositoryPort orderRepository,
                                                   CartRepositoryPort cartRepository) {
        return new AuthorizationFacade(offerRepository, orderRepository, cartRepository);
    }
}
