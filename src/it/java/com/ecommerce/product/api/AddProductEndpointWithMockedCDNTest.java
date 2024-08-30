package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.product.api.dto.AddProductRequest;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {"cdn.enabled=false"})
public class AddProductEndpointWithMockedCDNTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should add new product with all data")
    public void shouldAddNewProductWithAllData() {

        // given
        String ADDED_PRODUCT_NAME = "added-product-name";
        String ADDED_PRODUCT_EAN = "1112233";
        String ADDED_PRODUCT_CATEGORY = "BEAUTY";
        String ADDED_PRODUCT_DESCRIPTION = "added-product-description";
        AddProductRequest productRequest = new AddProductRequest(
                ADDED_PRODUCT_NAME,
                ADDED_PRODUCT_EAN,
                ADDED_PRODUCT_CATEGORY,
                IMAGE,
                ADDED_PRODUCT_DESCRIPTION
        );

        // when
        ResponseEntity<ProductResponse> response = productAbility().addProduct(productRequest, credentials(EMAIL));

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(ADDED_PRODUCT_NAME, response.getBody().getName());
        assertEquals(ADDED_PRODUCT_EAN, response.getBody().getEan());
        assertEquals(ADDED_PRODUCT_CATEGORY, response.getBody().getCategory());
        assertTrue(response.getBody().getImageUrl().matches(MOCK_IMAGE_NAME_PATTERN));
        assertEquals(ADDED_PRODUCT_DESCRIPTION, response.getBody().getDescription());
        assertNotNull(response.getBody().getVersion());

        // and verify that image is not uploaded to CDN
        cdnAbility().verifyUploadImageWasNotCalled();
    }
}
