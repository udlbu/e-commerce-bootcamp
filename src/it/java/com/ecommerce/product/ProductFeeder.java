package com.ecommerce.product;

import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.shared.Ability;
import org.springframework.http.ResponseEntity;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE;
import static com.ecommerce.shared.CredentialsAbility.credentials;

public interface ProductFeeder extends Ability {

    default ResponseEntity<ProductResponse> addSampleProduct() {
        String ADDED_PRODUCT_NAME = "added-product-name";
        String ADDED_PRODUCT_EAN = "111";
        String ADDED_PRODUCT_CATEGORY = "SPORTS";
        String ADDED_PRODUCT_DESCRIPTION = "added-product-description";
        AddProductRequest addProductRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                ADDED_PRODUCT_EAN,
                ADDED_PRODUCT_CATEGORY,
                IMAGE,
                ADDED_PRODUCT_DESCRIPTION
        );
        return productAbility().addProduct(addProductRequest, credentials(EMAIL));
    }
}
