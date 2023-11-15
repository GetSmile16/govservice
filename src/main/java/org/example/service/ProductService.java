package org.example.service;

import jakarta.validation.Valid;
import org.example.dto.product.NewProductDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductIdDto;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ProductService {
    List<ProductDto> getAllProducts();

    ProductIdDto addProduct(@Valid NewProductDto productDto);
}
