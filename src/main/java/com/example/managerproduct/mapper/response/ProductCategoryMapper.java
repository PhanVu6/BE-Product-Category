package com.example.managerproduct.mapper.response;


import com.example.managerproduct.dto.response.ProductCategoryDto;
import com.example.managerproduct.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    ProductCategoryMapper INSTANCE = Mappers.getMapper(ProductCategoryMapper.class);

    ProductCategory toEntity(ProductCategoryDto productDto);

    ProductCategoryDto toDto(ProductCategory product);

    List<ProductCategoryDto> DTO_LIST(List<ProductCategory> products);

    List<ProductCategory> ENTITY_LIST(List<ProductCategoryDto> productDtos);
}
