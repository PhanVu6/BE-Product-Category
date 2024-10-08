package com.example.managerproduct.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String productCode;

    private Long quantity;

    private String imageLink;

    private String status;

    private Date createdDate;

    private Date modifiedDate;

    private String createdBy;

    private String modifiedBy;

    private List<CategoryDto> categories;

    private List<ImageProductDto> imageProducts;
    private String nameCategory;
}
