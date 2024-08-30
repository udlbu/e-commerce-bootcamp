package com.ecommerce.order.adapter;

import com.ecommerce.offer.adapter.OfferEntity;
import com.ecommerce.order.domain.OrderLine;
import com.ecommerce.order.domain.OrderLineId;
import com.ecommerce.shared.domain.Quantity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "ORDER_LINES")
@Data
public class OrderLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private OrderEntity order;

    @OneToOne
    @JoinColumn(name = "OFFER_ID", nullable = false)
    private OfferEntity offer;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    public static OrderLineEntity newLine(OrderLine orderLine, OrderEntity orderEntity, OfferEntity offerEntity) {
        OrderLineEntity orderLineEntity = new OrderLineEntity();
        orderLineEntity.setOrder(orderEntity);
        orderLineEntity.setOffer(offerEntity);
        orderLineEntity.setQuantity(orderLine.quantity().val());
        return orderLineEntity;
    }

    public OrderLine toDomain() {
        return new OrderLine(
                new OrderLineId(id),
                offer.toDomain(),
                new Quantity(quantity),
                new com.ecommerce.shared.domain.Version(version)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineEntity that = (OrderLineEntity) o;
        return id.equals(that.id) && offer.equals(that.offer) && quantity.equals(that.quantity) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, offer, quantity, version);
    }
}
