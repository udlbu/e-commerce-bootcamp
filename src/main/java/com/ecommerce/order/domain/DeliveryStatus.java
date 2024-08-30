package com.ecommerce.order.domain;

public enum DeliveryStatus {
    NEW {
        @Override
        public DeliveryStatus next() {
            return PREPARING;
        }
    }, PREPARING {
        @Override
        public DeliveryStatus next() {
            return IN_TRANSIT;
        }
    }, IN_TRANSIT {
        @Override
        public DeliveryStatus next() {
            return DELIVERED;
        }
    }, DELIVERED {
        @Override
        public DeliveryStatus next() {
            return DELIVERED;
        }
    };

    public abstract DeliveryStatus next();
}
