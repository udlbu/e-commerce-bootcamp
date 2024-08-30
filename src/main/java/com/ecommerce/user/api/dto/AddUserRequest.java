package com.ecommerce.user.api.dto;

import com.ecommerce.user.domain.model.FirstName;
import com.ecommerce.user.domain.model.LastName;
import com.ecommerce.user.domain.model.Password;
import com.ecommerce.user.domain.model.TaxId;
import com.ecommerce.user.domain.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String taxId;

    @Valid
    private AddUserAddress address;

    public User toDomain() {
        return new User(
                null,
                new com.ecommerce.user.domain.model.Email(email),
                new FirstName(firstName),
                new LastName(lastName),
                taxId != null ? new TaxId(taxId) : null,
                null,
                address != null ? address.toDomain() : null
        );
    }

    public Password toPassword() {
        return new Password(password);
    }
}
