package com.example.managerproduct.mapper.request;

import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateProductMapper {
    UpdateProductMapper INSTANCE = Mappers.getMapper(UpdateProductMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Product toEntity(UpdateProductDto productDto);

    void updateProductFromDto(UpdateProductDto dto, @MappingTarget Product product);
}
