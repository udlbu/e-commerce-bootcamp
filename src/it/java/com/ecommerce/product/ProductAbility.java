package com.ecommerce.product;

import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ModifyProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.product.api.dto.ProductsPageResponse;
import com.ecommerce.shared.HttpAbility;
import com.ecommerce.shared.api.dto.Errors;
import com.ecommerce.shared.domain.Credentials;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ProductAbility extends HttpAbility {

    private final String URL = "/api/products";

    public ProductAbility(TestRestTemplate testRestTemplate, String authServerUrl) {
        super(testRestTemplate, authServerUrl);
    }

    public ResponseEntity<ProductResponse> getProduct(Long id, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(accept(credentials)), ProductResponse.class);
    }

    public ResponseEntity<Errors> getProductUnauthorized(Long id, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.GET, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<ProductsPageResponse> getProducts(int page, int pageSize, Credentials credentials) {
        return testRestTemplate.exchange(URL + "?page=" + page + "&pageSize=" + pageSize, HttpMethod.GET, new HttpEntity<>(accept(credentials)), ProductsPageResponse.class);
    }

    public ResponseEntity<ProductsPageResponse> getProducts(Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(accept(credentials)), ProductsPageResponse.class);
    }

    public ResponseEntity<Errors> getProductsUnauthorized(String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(acceptUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<ProductResponse> addProduct(AddProductRequest productRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(productRequest, acceptAndContentType(credentials)), ProductResponse.class);
    }

    public ResponseEntity<Errors> addProductUnauthorized(AddProductRequest productRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(productRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Errors> addProductError(AddProductRequest productRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(productRequest, acceptAndContentType(credentials)), Errors.class);
    }

    public ResponseEntity<Void> deleteProduct(Long id, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(authorization(credentials)), Void.class);
    }

    public ResponseEntity<Errors> deleteProductError(Long id, Credentials credentials) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(authorization(credentials)), Errors.class);
    }

    public ResponseEntity<Errors> deleteProductUnauthorized(Long id, String invalidToken) {
        return testRestTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(invalidOrMissingToken(invalidToken)), Errors.class);
    }

    public ResponseEntity<ProductResponse> modifyProduct(ModifyProductRequest productRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(productRequest, acceptAndContentType(credentials)), ProductResponse.class);
    }

    public ResponseEntity<Errors> modifyProductUnauthorized(ModifyProductRequest productRequest, String invalidToken) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(productRequest, acceptAndContentTypeUnauthorized(invalidToken)), Errors.class);
    }

    public ResponseEntity<Errors> modifyProductError(ModifyProductRequest productRequest, Credentials credentials) {
        return testRestTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(productRequest, acceptAndContentType(credentials)), Errors.class);
    }
}
