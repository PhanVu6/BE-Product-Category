package com.example.managerproduct.service;

import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductCategoryDto;

import java.util.List;

public interface IProductCategoryService {
    ApiResponse<List<ProductCategoryDto>> getAllProduct();

    ApiResponse<ProductCategoryDto> create(ProductCategoryDto productDto);

    ApiResponse<ProductCategoryDto> update(ProductCategoryDto productDto);

    ApiResponse<Boolean> delete(ProductCategoryDto productDto);
}
