package com.ecommerce.order.adapter;

import com.ecommerce.order.domain.port.OrderRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
public class ChangeDeliveryStatusScheduler {

    private final static Logger LOG = LoggerFactory.getLogger(ChangeDeliveryStatusScheduler.class);

    private final OrderRepositoryPort repository;

    public ChangeDeliveryStatusScheduler(OrderRepositoryPort repository) {
        this.repository = repository;
    }

    @Async
    @Scheduled(cron = "0 * * * * ?") // every minute
    public void changeDeliveryStatus() {
        LOG.info("Scheduling change order delivery task");
        repository.changeOrderDeliveryStatus();
    }
}
