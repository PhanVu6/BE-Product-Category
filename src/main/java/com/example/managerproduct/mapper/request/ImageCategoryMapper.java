package com.example.managerproduct.mapper.request;

import com.example.managerproduct.dto.response.ImageCategoryDto;
import com.example.managerproduct.entity.ImageCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImageCategoryMapper {
    ImageCategoryMapper INSTANCE = Mappers.getMapper(ImageCategoryMapper.class);

    ImageCategory toEntity(ImageCategoryDto dto);

    ImageCategoryDto toDto(ImageCategory imageProduct);

    List<ImageCategoryDto> DTO_LIST(List<ImageCategory> list);
}
