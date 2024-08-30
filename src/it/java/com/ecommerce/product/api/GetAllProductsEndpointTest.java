package com.ecommerce.product.api;

import com.ecommerce.BaseIntegrationTest;
import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ProductsPageResponse;
import com.ecommerce.shared.api.dto.Errors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import static com.ecommerce.IntegrationTestData.EMAIL;
import static com.ecommerce.IntegrationTestData.NAME;
import static com.ecommerce.shared.CredentialsAbility.credentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class GetAllProductsEndpointTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should return first and then second page of the available products")
    public void shouldReturnFirstAndSecondPageOfTheAvailableProducts() {
        // given
        productAbility().addProduct(new AddProductRequest(
                "product-name-1",
                "product-ean-1",
                "BEAUTY",
                null,
                null
        ), credentials(EMAIL));
        productAbility().addProduct(new AddProductRequest(
                "product-name-2",
                "product-ean-2",
                "BOOKS",
                null,
                null
        ), credentials(EMAIL));
        int PAGE_SIZE = 2;

        // when
        int FIRST_PAGE = 0;
        ResponseEntity<ProductsPageResponse> firstPageResponse = productAbility().getProducts(FIRST_PAGE, PAGE_SIZE, credentials(EMAIL));

        // then
        assertTrue(firstPageResponse.getStatusCode().is2xxSuccessful());
        assertTrue(firstPageResponse.hasBody());
        assertEquals(2, firstPageResponse.getBody().getProducts().size());
        assertEquals(NAME, firstPageResponse.getBody().getProducts().get(0).getName());
        assertEquals("product-name-1", firstPageResponse.getBody().getProducts().get(1).getName());
        // two added here and one from flyway initial script
        assertEquals(3, firstPageResponse.getBody().getTotal());

        // and when
        int SECOND_PAGE = 1;
        ResponseEntity<ProductsPageResponse> secondPageResponse = productAbility().getProducts(SECOND_PAGE, PAGE_SIZE, credentials(EMAIL));
        assertTrue(secondPageResponse.getStatusCode().is2xxSuccessful());
        assertTrue(secondPageResponse.hasBody());
        assertEquals(1, secondPageResponse.getBody().getProducts().size());
        assertEquals("product-name-2", secondPageResponse.getBody().getProducts().get(0).getName());
        assertEquals(3, firstPageResponse.getBody().getTotal());
    }

    @Test
    @DisplayName("should return first page of the available products when page and pageSize parameters are absent")
    public void shouldReturnFirstPageOfTheAvailableProductsWhenQueryParametersAreAbsent() {
        // given
        productAbility().addProduct(new AddProductRequest(
                "product-name-1",
                "product-ean-1",
                "BEAUTY",
                null,
                null
        ), credentials(EMAIL));
        productAbility().addProduct(new AddProductRequest(
                "product-name-2",
                "product-ean-2",
                "BOOKS",
                null,
                null
        ), credentials(EMAIL));

        // when
        ResponseEntity<ProductsPageResponse> firstPageResponse = productAbility().getProducts(credentials(EMAIL));

        // then
        assertTrue(firstPageResponse.getStatusCode().is2xxSuccessful());
        assertTrue(firstPageResponse.hasBody());
        assertEquals(3, firstPageResponse.getBody().getProducts().size());
        assertEquals(3, firstPageResponse.getBody().getTotal());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token"})
    @NullAndEmptySource
    @DisplayName("should return UNAUTHORIZED error code when request does not contain authentication token")
    public void shouldReturnUnauthorizedWhenRequestDoesNotContainAuthToken(String invalidTokenOrNullOrEmpty) {

        // when
        ResponseEntity<Errors> response = productAbility().getProductsUnauthorized(invalidTokenOrNullOrEmpty);

        // then
        assertEquals(UNAUTHORIZED, response.getStatusCode());
    }
}
