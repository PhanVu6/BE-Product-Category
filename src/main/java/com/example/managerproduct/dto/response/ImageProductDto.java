package com.example.managerproduct.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageProductDto {
    private Long id;

    private String imageName;

    private String imagePath;
}
