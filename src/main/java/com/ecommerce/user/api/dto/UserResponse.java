package com.ecommerce.user.api.dto;

import com.ecommerce.user.domain.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String taxId;
    private Long version;
    private AddressResponse address;

    public static UserResponse of(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.id().val());
        response.setEmail(user.email().val());
        response.setFirstName(user.firstName().val());
        response.setLastName(user.lastName().val());
        response.setTaxId(user.taxId() != null ? user.taxId().val() : null);
        response.setVersion(user.version().val());
        response.setAddress(user.address() != null ? AddressResponse.of(user.address()) : null);
        return response;
    }
}
