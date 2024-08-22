package com.example.managerproduct.mapper.request;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateProductMapper {
    CreateProductMapper INSTANCE = Mappers.getMapper(CreateProductMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Product toEntity(CreateProductDto productDto);
}
