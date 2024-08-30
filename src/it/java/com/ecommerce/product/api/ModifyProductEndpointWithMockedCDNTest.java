package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.order.OrderFeeder;
import com.ecommerce.product.api.dto.ModifyProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.IMAGE;
import static com.ecommerce.IntegrationTestData.MOCK_IMAGE_NAME_PATTERN;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {"cdn.enabled=false"})
public class ModifyProductEndpointWithMockedCDNTest extends BaseIntegrationTest implements OrderFeeder {

    @Test
    @DisplayName("should update all product data and increment version")
    public void shouldUpdateAllProductDataAndIncrementVersion() {

        // given
        ResponseEntity<ProductResponse> newProduct = addSampleProduct();

        String MODIFIED_PRODUCT_NAME = "modified-product-name";
        String MODIFIED_PRODUCT_EAN = "222";
        String MODIFIED_PRODUCT_CATEGORY = "BOOKS";
        String MODIFIED_PRODUCT_DESCRIPTION = "modified-product-description";
        ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
                newProduct.getBody().getId(),
                MODIFIED_PRODUCT_NAME,
                MODIFIED_PRODUCT_EAN,
                MODIFIED_PRODUCT_CATEGORY,
                IMAGE,
                MODIFIED_PRODUCT_DESCRIPTION,
                newProduct.getBody().getVersion()
        );

        // verify image was not uploaded during adding product
        cdnAbility().verifyUploadImageWasNotCalled();
        cdnAbility().reset();

        // when
        ResponseEntity<ProductResponse> updatedProduct = productAbility().modifyProduct(modifyProductRequest, credentials(EMAIL));

        // then
        assertTrue(updatedProduct.getStatusCode().is2xxSuccessful());
        assertTrue(updatedProduct.hasBody());
        assertEquals(newProduct.getBody().getId(), updatedProduct.getBody().getId());
        assertEquals(MODIFIED_PRODUCT_NAME, updatedProduct.getBody().getName());
        assertEquals(MODIFIED_PRODUCT_EAN, updatedProduct.getBody().getEan());
        assertEquals(MODIFIED_PRODUCT_CATEGORY, updatedProduct.getBody().getCategory());
        assertTrue(updatedProduct.getBody().getImageUrl().matches(MOCK_IMAGE_NAME_PATTERN));
        assertEquals(MODIFIED_PRODUCT_DESCRIPTION, updatedProduct.getBody().getDescription());
        assertEquals(newProduct.getBody().getVersion() + 1, updatedProduct.getBody().getVersion());

        // cdn
        cdnAbility().verifyUploadImageWasNotCalled();
    }
}
