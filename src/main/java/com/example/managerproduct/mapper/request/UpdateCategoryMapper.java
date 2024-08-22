package com.example.managerproduct.mapper.request;

import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.entity.Category;
import com.example.managerproduct.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateCategoryMapper {
    UpdateCategoryMapper INSTANCE = Mappers.getMapper(UpdateCategoryMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Product toEntity(UpdateCategoryDto productDto);

    void updateCategoryFromDto(UpdateCategoryDto dto, @MappingTarget Category category);
}
