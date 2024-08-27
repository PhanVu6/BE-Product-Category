package com.example.managerproduct.service;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    ApiResponse<Page<CategoryDto>> getAllCategory(String name, Pageable pageable);

    ApiResponse<List<CategoryDto>> open(String name);

    ApiResponse<CategoryDto> create(CreateCategoryDto categoryDto, String createBy);

    ApiResponse<CategoryDto> update(UpdateCategoryDto categoryDto, String modifiedBy);

    ApiResponse<Boolean> delete(Long id);
}
