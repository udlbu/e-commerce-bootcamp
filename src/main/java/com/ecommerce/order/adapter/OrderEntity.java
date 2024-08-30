package com.ecommerce.order.adapter;

import com.ecommerce.offer.adapter.OfferEntity;
import com.ecommerce.order.domain.CreatedAt;
import com.ecommerce.order.domain.DeliveryMethod;
import com.ecommerce.order.domain.DeliveryStatus;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderId;
import com.ecommerce.order.domain.OrderLine;
import com.ecommerce.order.domain.OrderStatus;
import com.ecommerce.order.domain.PaymentMethod;
import com.ecommerce.order.domain.PaymentStatus;
import com.ecommerce.order.domain.UpdatedAt;
import com.ecommerce.user.domain.model.UserId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "ORDERS")
@Data
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BUYER_ID", nullable = false)
    private Long buyerId;

    @Column(name = "DELIVERY_METHOD", nullable = false)
    private String deliveryMethod;

    @Column(name = "DELIVERY_STATUS", nullable = false)
    private String deliveryStatus;

    @Column(name = "PAYMENT_METHOD", nullable = false)
    private String paymentMethod;

    @Column(name = "PAYMENT_STATUS", nullable = false)
    private String paymentStatus;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderLineEntity> lines;

    public OrderEntity changeStatus(OrderStatus status, Instant changedAt) {
        this.status = status.name();
        this.updatedAt = changedAt;
        return this;
    }

    public OrderEntity changeDeliveryStatus(DeliveryStatus deliveryStatus, Instant changedAt) {
        this.deliveryStatus = deliveryStatus.name();
        this.updatedAt = changedAt;
        return this;
    }

    public Order toDomain() {
        return new Order(
                new OrderId(id),
                new UserId(buyerId),
                DeliveryMethod.valueOf(deliveryMethod),
                DeliveryStatus.valueOf(deliveryStatus),
                PaymentMethod.valueOf(paymentMethod),
                PaymentStatus.valueOf(paymentStatus),
                OrderStatus.valueOf(status),
                new CreatedAt(createdAt),
                new UpdatedAt(updatedAt),
                new com.ecommerce.shared.domain.Version(version),
                lines.stream()
                        .map(OrderLineEntity::toDomain)
                        .collect(Collectors.toList()));
    }

    public static OrderEntity newOrder(Order order, Iterable<OfferEntity> offerEntities) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setBuyerId(order.buyerId().val());
        orderEntity.setDeliveryMethod(order.deliveryMethod().name());
        orderEntity.setDeliveryStatus(order.deliveryStatus().name());
        orderEntity.setPaymentMethod(order.paymentMethod().name());
        orderEntity.setPaymentStatus(order.paymentStatus().name());
        orderEntity.setStatus(order.status().name());
        orderEntity.setCreatedAt(order.createdAt().val());
        orderEntity.setUpdatedAt(order.updatedAt().val());
        orderEntity.setLines(
                order.lines()
                        .stream()
                        .map(it -> OrderLineEntity.newLine(it, orderEntity, findOffer(it, offerEntities))).collect(Collectors.toSet())
        );
        return orderEntity;
    }

    private static OfferEntity findOffer(OrderLine line, Iterable<OfferEntity> offerEntities) {
        for (OfferEntity offerEntity : offerEntities) {
            if (offerEntity.getId().equals(line.offer().id().val())) {
                return offerEntity;
            }
        }
        throw new IllegalStateException(String.format("Offer <%s> for order line must exits", line.offer().id()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return id.equals(that.id) && buyerId.equals(that.buyerId) && deliveryMethod.equals(that.deliveryMethod) && deliveryStatus.equals(that.deliveryStatus) && paymentMethod.equals(that.paymentMethod) && paymentStatus.equals(that.paymentStatus) && createdAt.equals(that.createdAt) && updatedAt.equals(that.updatedAt) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, buyerId, deliveryMethod, deliveryStatus, paymentMethod, paymentStatus, createdAt, updatedAt, version);
    }
}
