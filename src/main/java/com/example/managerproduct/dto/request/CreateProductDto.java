package com.example.managerproduct.dto.request;

import com.example.managerproduct.dto.response.CategoryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {
    private String name;
    private String description;
    private Double price;
    private String product_code;
    private Long quantity;
    private String status;
    private List<CategoryDto> categories;
}
