package com.example.managerproduct.mapper.response;

import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductDto productDto);

    ProductDto toDto(Product product);

    List<ProductDto> DTO_LIST(List<Product> products);

    List<Product> ENTITY_LIST(List<ProductDto> productDtos);
}
