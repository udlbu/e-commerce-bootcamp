package com.ecommerce.user;

import com.ecommerce.shared.Ability;
import com.ecommerce.user.api.dto.AddUserAddress;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;

import static com.ecommerce.IntegrationTestData.PASSWORD;

public interface UserFeeder extends Ability {

    default UserResponse addExampleUser() {
        return userAbility().addUser(new AddUserRequest(
                "example@mail.com",
                PASSWORD,
                "Tom",
                "Example",
                "TAX123",
                new AddUserAddress(
                        "England",
                        "London",
                        "Ashley Road",
                        "10",
                        "129a",
                        "23456"
                )
        )).getBody();
    }
}
