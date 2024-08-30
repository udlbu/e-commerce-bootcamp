package com.ecommerce.product.api;

import com.ecommerce.product.api.dto.AddProductRequest;
import com.ecommerce.product.api.dto.ModifyProductRequest;
import com.ecommerce.product.api.dto.ProductResponse;
import com.ecommerce.product.api.dto.ProductsPageResponse;
import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.product.domain.ProductFacade;
import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductId;
import com.ecommerce.shared.domain.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ecommerce.product.api.dto.ProductResponse.of;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
public class ProductEndpoint {

    private final ProductFacade facade;

    private final CdnProperties properties;

    public ProductEndpoint(ProductFacade facade, CdnProperties properties) {
        this.facade = facade;
        this.properties = properties;
    }

    @GetMapping(value = "/products/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long id, HttpServletRequest request) {
        Product product = facade.getProduct(new ProductId(id));
        if (product == null) {
            return notFound().build();
        }
        return ok(of(product, properties.getImgUrl(request)));
    }

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductsPageResponse> getProduct(HttpServletRequest request,
                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        PageResult<Product> products = facade.getProducts(page, pageSize);
        return ok(ProductsPageResponse.of(products, properties.getImgUrl(request)));
    }

    @PostMapping(value = "/products",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody AddProductRequest productRequest, HttpServletRequest request) {
        Product product = facade.addProduct(productRequest.toDomain(), productRequest.toImage());
        return ok(of(product, properties.getImgUrl(request)));
    }

    @DeleteMapping(value = "/products/{id}")
    public void deleteProduct(@PathVariable("id") Long id) {
        facade.deleteProduct(new ProductId(id));
    }

    @PutMapping(value = "/products",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> modifyProduct(@Valid @RequestBody ModifyProductRequest productRequest, HttpServletRequest request) {
        facade.modifyProduct(productRequest.toDomain(), productRequest.toImage());
        Product product = facade.getProduct(new ProductId(productRequest.getId()));
        if (product == null) {
            return notFound().build();
        }
        return ok(of(product, properties.getImgUrl(request)));
    }
}