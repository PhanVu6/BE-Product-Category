package com.example.managerproduct.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDto {
    private String name;

    private String description;

    private String category_code;

    private String status;
}
