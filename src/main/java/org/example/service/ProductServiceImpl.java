package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.product.NewProductDto;
import org.example.dto.product.ProductDto;
import org.example.dto.product.ProductIdDto;
import org.example.exception.product.ProductIsExist;
import org.example.mapper.ProductMapper;
import org.example.model.Product;
import org.example.model.SeasonProduct;
import org.example.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final ProductRepository productRepository;
    private final MessageSource messageSource;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, MessageSource messageSource) {
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(o ->
                        ProductMapper.INSTANCE.productToProductDto(
                                o,
                                o.getSeasonProduct() != null
                        )
                ).toList();
    }

    @Override
    @Transactional
    public ProductIdDto addProduct(NewProductDto productDto) {
        if (productRepository.findProductByProductName(productDto.getProductName()) != null) {
            String message = messageSource.getMessage("product.is_exist", null, Locale.getDefault());
            log.warn(message);
            throw new ProductIsExist(message);
        }

        SeasonProduct seasonProduct = null;

        Integer remainingCount = productDto.getRemainingCount();
        if (remainingCount != null) {
            seasonProduct = new SeasonProduct();
            seasonProduct.setRemainingCount(remainingCount);
        }

        Product product = ProductMapper.INSTANCE.newProductDtoToProduct(productDto, seasonProduct);

        Product save = productRepository.save(product);

        String seasonStr = "Product";
        if (seasonProduct != null) {
            seasonStr = "Season product";
        }
        log.info(seasonStr + " with id \"" + save.getId() + "\" created");

        return new ProductIdDto(save.getId());
    }
}
