package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.example.dto.product.NewProductDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductIdDto;
import org.example.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@RequestMapping(value = "products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
    private final ProductServiceImpl productService;

    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create new service. If field \"remainingTime\" isn't empty, then creates seasonal service")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @RequestBody(content = @Content(examples = {
            @ExampleObject(
                    summary = "New season service",
                    value =
                            "{\"productName\": \"Test name\","
                                    + "\"remainingCount\": \"10\"" +
                                    "}"),
            @ExampleObject(
                    summary = "New common service",
                    value =
                            "{\"productName\": \"Test name\"}")
    }))
    public ResponseEntity<ProductIdDto> createProduct(@RequestBody NewProductDto newProductDto) {
        return ResponseEntity.ok(productService.addProduct(newProductDto));
    }

    @Operation(summary = "Get all available services")
    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
