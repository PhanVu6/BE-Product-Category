package com.example.managerproduct.dto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDto {
    private Long id;

    private Date createdDate;

    private Date modifiedDate;

    private String createdBy;

    private String modifiedBy;

}
