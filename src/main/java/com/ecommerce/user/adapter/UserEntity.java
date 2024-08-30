package com.ecommerce.user.adapter;

import com.ecommerce.user.domain.model.Email;
import com.ecommerce.user.domain.model.FirstName;
import com.ecommerce.user.domain.model.LastName;
import com.ecommerce.user.domain.model.Status;
import com.ecommerce.user.domain.model.TaxId;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Table(name = "USERS")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMAIL", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "FIRST_NAME", length = 50, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50, nullable = false)
    private String lastName;

    @Column(name = "TAX_ID", length = 50)
    private String taxId;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private AddressEntity address;

    public User toDomain() {
        return new User(new UserId(id), new Email(email), new FirstName(firstName), new LastName(lastName), taxId != null ? new TaxId(taxId) : null, new com.ecommerce.shared.domain.Version(version), address != null ? address.toDomain() : null);
    }

    public static UserEntity newUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setEmail(user.email().val().toLowerCase());
        entity.setFirstName(user.firstName().val());
        entity.setLastName(user.lastName().val());
        entity.setTaxId(user.taxId() != null ? user.taxId().val() : null);
        entity.setStatus(Status.INACTIVE.name());
        if (user.address() != null) {
            AddressEntity addressEntity = AddressEntity.newAddress(user.address());
            addressEntity.setUser(entity);
            entity.setAddress(addressEntity);
        }
        return entity;
    }

    public UserEntity merge(User user) {
        setEmail(user.email().val().toLowerCase());
        setFirstName(user.firstName().val());
        setLastName(user.lastName().val());
        setTaxId(user.taxId() != null ? user.taxId().val() : null);
        if (user.address() == null && address == null) {
            return this;
        }
        if (user.address() != null && address != null) {
            address.merge(user.address());
        } else if (user.address() != null) {
            AddressEntity addressEntity = AddressEntity.newAddress(user.address());
            addressEntity.setUser(this);
            setAddress(addressEntity);
        } else {
            address.setUser(null);
            setAddress(null);
        }
        return this;
    }
}
