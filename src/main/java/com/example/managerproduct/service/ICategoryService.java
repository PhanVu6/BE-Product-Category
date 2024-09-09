package com.example.managerproduct.service;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ICategoryService {
    ApiResponse<Page<CategoryDto>> getAllCategory(String name, String status, String categoryCode, LocalDate startDate, LocalDate endDate, Pageable pageable);

    ApiResponse<List<CategoryDto>> open(String name);

    ApiResponse<CategoryDto> getById(Long id);

    ApiResponse<CategoryDto> create(CreateCategoryDto categoryDto, String createBy);

    @Transactional
    ApiResponse<CategoryDto> createCategory(CreateCategoryDto categoryDto, MultipartFile[] images, String createBy);

    @Transactional
    ApiResponse<CategoryDto> updateCategoryImages(UpdateCategoryDto categoryDto, MultipartFile[] images, String modifiedBy);

    ApiResponse<CategoryDto> update(UpdateCategoryDto categoryDto, String modifiedBy);

    @Transactional
    ApiResponse<CategoryDto> deleteMem(Long id);

    ApiResponse<Boolean> delete(Long id);
}
