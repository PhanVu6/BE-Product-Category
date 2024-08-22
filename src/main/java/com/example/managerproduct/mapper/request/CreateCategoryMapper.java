package com.example.managerproduct.mapper.request;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateCategoryMapper {
    CreateCategoryMapper INSTANCE = Mappers.getMapper(CreateCategoryMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Category toEntity(CreateCategoryDto productDto);
}
