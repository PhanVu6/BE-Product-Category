package com.example.managerproduct.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String product_code;
    private Long quantity;
    private String status;

}
