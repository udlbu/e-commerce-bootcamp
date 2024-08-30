package com.ecommerce.user.adapter;

import com.ecommerce.user.domain.model.Address;
import com.ecommerce.user.domain.model.AddressId;
import com.ecommerce.user.domain.model.BuildingNo;
import com.ecommerce.user.domain.model.City;
import com.ecommerce.user.domain.model.Country;
import com.ecommerce.user.domain.model.FlatNo;
import com.ecommerce.user.domain.model.PostalCode;
import com.ecommerce.user.domain.model.Street;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Table(name = "ADDRESSES")
@Data
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COUNTRY", length = 50, nullable = false)
    private String country;

    @Column(name = "CITY", length = 50, nullable = false)
    private String city;

    @Column(name = "STREET", length = 50, nullable = false)
    private String street;

    @Column(name = "BUILDING_NO", length = 10, nullable = false)
    private String buildingNo;

    @Column(name = "FLAT_NO", length = 10)
    private String flatNo;

    @Column(name = "POSTAL_CODE", length = 10, nullable = false)
    private String postalCode;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    public Address toDomain() {
        return new Address(
                new AddressId(id),
                new Country(country),
                new City(city),
                new Street(street),
                new BuildingNo(buildingNo),
                flatNo != null ? new FlatNo(flatNo) : null,
                new PostalCode(postalCode),
                new com.ecommerce.shared.domain.Version(version)
        );
    }

    public static AddressEntity newAddress(Address address) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCountry(address.country().val());
        addressEntity.setCity(address.city().val());
        addressEntity.setStreet(address.street().val());
        addressEntity.setBuildingNo(address.buildingNo().val());
        addressEntity.setFlatNo(address.flatNo() != null ? address.flatNo().val() : null);
        addressEntity.setPostalCode(address.postalCode().val());
        return addressEntity;
    }

    public AddressEntity merge(Address address) {
        setCountry(address.country().val());
        setCity(address.city().val());
        setStreet(address.street().val());
        setBuildingNo(address.buildingNo().val());
        setFlatNo(address.flatNo() != null ? address.flatNo().val() : null);
        setPostalCode(address.postalCode().val());
        return this;
    }
}
