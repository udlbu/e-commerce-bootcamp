package com.ecommerce.order;

import com.ecommerce.cart.api.dto.AddItemToCartRequest;
import com.ecommerce.cart.api.dto.CartResponse;
import com.ecommerce.offer.api.dto.AddOfferRequest;
import com.ecommerce.offer.api.dto.OfferResponse;
import com.ecommerce.order.api.dto.SubmitOrderRequest;
import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.Ability;
import com.ecommerce.user.api.dto.AddUserAddress;
import com.ecommerce.user.api.dto.AddUserRequest;
import com.ecommerce.user.api.dto.UserResponse;

import java.math.BigDecimal;

import static com.ecommerce.IntegrationTestData.DELIVERY_METHOD_FEDEX;
import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE;
import static com.ecommerce.IntegrationTestData.ITEM_QUANTITY_ONE;
import static com.ecommerce.IntegrationTestData.PASSWORD;
import static com.ecommerce.IntegrationTestData.PAYMENT_METHOD_CARD;
import static com.ecommerce.shared.CredentialsAbility.credentials;

public interface OrderFeeder extends Ability {

    default ProductResponse addIphoneProduct() {
        return productAbility().addProduct(new AddProductRequest(
                "Iphone",
                "6895330",
                "ELECTRONICS",
                IMAGE,
                "Iphone 12 Pro 128GB"
        ), credentials(EMAIL)).getBody();
    }

    default ProductResponse addMacbookProduct() {
        return productAbility().addProduct(new AddProductRequest(
                "Macbook Pro M2",
                "92212589",
                "ELECTRONICS",
                IMAGE,
                "Macbook 16 Pro M2 512GB 32GB ram"
        ), credentials(EMAIL)).getBody();
    }

    default ProductResponse addIpadProduct() {
        return productAbility().addProduct(new AddProductRequest(
                "Ipad Pro 11",
                "39856732",
                "ELECTRONICS",
                IMAGE,
                "Ipad Pro 11 256GB WiFi"
        ), credentials(EMAIL)).getBody();
    }

    default ProductResponse addSamsungGalaxyProduct() {
        return productAbility().addProduct(new AddProductRequest(
                "Samsung Galaxy 13",
                "4435677",
                "ELECTRONICS",
                IMAGE,
                "Samsung Galaxy 13 512GB WiFi"
        ), credentials(EMAIL)).getBody();
    }

    default UserResponse addSellerUser() {
        return userAbility().addUser(new AddUserRequest(
                "seller@mail.com",
                PASSWORD,
                "Tom",
                "Seller",
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

    default UserResponse addBuyerUser(String firstName) {
        return userAbility().addUser(new AddUserRequest(
                firstName + "_buyer@mail.com",
                PASSWORD,
                firstName,
                "Buyer",
                "TAX333",
                new AddUserAddress(
                        "England",
                        "New Milton",
                        "English Street",
                        "9",
                        "2",
                        "r554gl"
                )
        )).getBody();
    }

    default OfferResponse addOffer(UserResponse seller, ProductResponse productResponse, int quantity, BigDecimal price) {
        OfferResponse offer = offerAbility().addOffer(new AddOfferRequest(
                seller.getId(),
                price,
                quantity,
                productResponse.getId()
        ), credentials(seller.getEmail())).getBody();
        return offerAbility().activateOffer(offer.getId(), credentials(seller.getEmail())).getBody();
    }

    default Long submitOrder(CartResponse cart, UserResponse buyer) {
        return orderAbility().submitOrder(new SubmitOrderRequest(
                cart.getId(),
                DELIVERY_METHOD_FEDEX.name(),
                PAYMENT_METHOD_CARD.name()
        ), credentials(buyer.getEmail())).getBody();
    }

    default CartResponse addOfferToCart(UserResponse buyer, OfferResponse offer) {
        cartAbility().addItemToCart(new AddItemToCartRequest(
                offer.getId(),
                buyer.getId(),
                ITEM_QUANTITY_ONE
        ), credentials(buyer.getEmail()));
        return cartAbility().getUserCart(buyer.getId(), credentials(buyer.getEmail())).getBody();
    }
}
