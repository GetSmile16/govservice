package org.example.mapper;

import org.example.dto.product.NewProductDto;
import org.example.dto.product.ProductDto;
import org.example.model.Product;
import org.example.model.SeasonProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "season", target = "season")
    ProductDto productToProductDto(Product product, boolean season);

    @Mapping(source = "seasonProduct", target = "seasonProduct")
    Product newProductDtoToProduct(NewProductDto newProductDto, SeasonProduct seasonProduct);
}
