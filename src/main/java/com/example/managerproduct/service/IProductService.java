package com.example.managerproduct.service;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface IProductService {

    ApiResponse<Page<ProductDto>> getAllProduct(String name, String status, String productCode, LocalDate startDate, LocalDate endDate, Pageable pageable);

    ApiResponse<Page<ProductDto>> open(String str, Pageable pageable);

    ApiResponse<ProductDto> getById(Long id);

    ApiResponse<ProductDto> create(CreateProductDto productDto, String createBy);

    ApiResponse<ProductDto> update(UpdateProductDto productDto, String modifiedBy);


    ApiResponse<ProductDto> create(CreateProductDto productDto, MultipartFile[] images, String createBy);

//    ApiResponse<ProductDto> update(UpdateProductDto productDto, MultipartFile[] images, String modifiedBy);

    @Transactional
    ApiResponse<ProductDto> update(UpdateProductDto productDto, MultipartFile[] images, List<Long> imageIdsToDelete, String modifiedBy);

    ApiResponse<ProductDto> deleteMem(Long id);

    ApiResponse<Boolean> delete(Long id);
}
