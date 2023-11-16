package org.example.service;

import org.example.dto.product.NewProductDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductIdDto;
import org.example.exception.product.ProductIsExist;
import org.example.model.Product;
import org.example.model.SeasonProduct;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MessageSource messageSource;
    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private final static Long ID = 1L;
    private final static String PRODUCT_NAME = "Test1";

    @Test
    void getAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(
                1L,
                "Test1",
                new SeasonProduct(1L, 10)
        ));
        products.add(new Product(
                2L,
                "Test2",
                null
        ));

        when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> actual = productService.getAllProducts();

        assertNotNull(actual);
        assertEquals(1L, actual.get(0).getId());
        assertEquals(2L, actual.get(1).getId());
        assertEquals("Test1", actual.get(0).getProductName());
        assertEquals("Test2", actual.get(1).getProductName());
    }

    @Test
    void addProduct_commonProduct_saveProduct() {
        NewProductDto productDto = new NewProductDto(PRODUCT_NAME);

        when(productRepository.findProductByProductName(productDto.getProductName())).thenReturn(null);

        Product savedProduct = new Product(ID, PRODUCT_NAME, null);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductIdDto actual = productService.addProduct(productDto);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());

        verify(productRepository, times(1)).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals(PRODUCT_NAME, capturedProduct.getProductName());
        assertNull(capturedProduct.getSeasonProduct());
    }

    @Test
    void addProduct_seasonProduct_saveProduct() {
        NewProductDto productDto = new NewProductDto(PRODUCT_NAME, 10);

        when(productRepository.findProductByProductName(productDto.getProductName())).thenReturn(null);

        Product savedProduct = new Product(
                ID,
                PRODUCT_NAME,
                new SeasonProduct(
                        ID,
                        10
                )
        );

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductIdDto actual = productService.addProduct(productDto);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());

        verify(productRepository, times(1)).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals(PRODUCT_NAME, capturedProduct.getProductName());
        assertNotNull(capturedProduct.getSeasonProduct());
        assertEquals(10, capturedProduct.getSeasonProduct().getRemainingCount());
    }

    @Test
    void addProduct_productIsExist_throwsException() {
        NewProductDto productDto = new NewProductDto(PRODUCT_NAME);

        when(productRepository.findProductByProductName(PRODUCT_NAME)).thenReturn(new Product());

        assertThrows(ProductIsExist.class, () -> productService.addProduct(productDto));

        verify(productRepository, never()).save(new Product());
    }
}