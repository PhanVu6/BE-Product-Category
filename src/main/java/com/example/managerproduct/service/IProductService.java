package com.example.managerproduct.service;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    ApiResponse<Page<ProductDto>> getAllProduct(String name, String productCode, java.sql.Date startDate, java.sql.Date endDate, Pageable pageable);

    ApiResponse<Page<ProductDto>> open(String str, Pageable pageable);

    ApiResponse<ProductDto> getById(Long id);

    ApiResponse<ProductDto> create(CreateProductDto productDto, String createBy);

    ApiResponse<ProductDto> update(UpdateProductDto productDto, String modifiedBy);

    ApiResponse<Boolean> delete(Long id);
}
