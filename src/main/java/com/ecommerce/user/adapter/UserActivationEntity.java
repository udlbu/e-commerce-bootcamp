package com.ecommerce.user.adapter;

import com.ecommerce.user.domain.model.ActivationStatus;
import com.ecommerce.user.domain.model.ActivationToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "USER_ACTIVATIONS")
@Data
public class UserActivationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "TOKEN", length = 200, nullable = false, unique = true)
    private String token;

    @Column(name = "STATUS", length = 10, nullable = false)
    private String status;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    public static UserActivationEntity newActivation(ActivationToken activationToken) {
        UserActivationEntity entity = new UserActivationEntity();
        entity.setUserId(activationToken.userId());
        entity.setStatus(ActivationStatus.NEW.name());
        entity.setToken(activationToken.token());
        entity.setCreatedAt(activationToken.createdAt());
        entity.setUpdatedAt(activationToken.createdAt());
        return entity;
    }
}
