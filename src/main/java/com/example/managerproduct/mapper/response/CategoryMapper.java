package com.example.managerproduct.mapper.response;

import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toEntity(CategoryDto productDto);

    CategoryDto toDto(Category product);

    List<CategoryDto> DTO_LIST(List<Category> products);

    List<Category> ENTITY_LIST(List<CategoryDto> productDtos);
}
