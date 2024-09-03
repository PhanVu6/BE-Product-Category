package com.example.managerproduct.mapper.request;

import com.example.managerproduct.dto.response.ImageProductDto;
import com.example.managerproduct.entity.ImageProduct;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImageProductMapper {
    ImageProductMapper INSTANCE = Mappers.getMapper(ImageProductMapper.class);

    ImageProduct toEntity(ImageProductDto dto);

    ImageProductDto toDto(ImageProduct imageProduct);

    List<ImageProductDto> DTO_LIST(List<ImageProduct> list);
}
