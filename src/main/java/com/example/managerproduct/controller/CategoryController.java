package com.example.managerproduct.controller;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.service.Impl.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<Page<CategoryDto>> getAllProduct(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.getAllCategory(name, pageable);
    }

    @PostMapping
    public ApiResponse<CategoryDto> create(@RequestBody CreateCategoryDto categoryDto, String createBy) {
        createBy = "admin";
        return categoryService.create(categoryDto, createBy);
    }

    @PutMapping
    public ApiResponse<CategoryDto> update(@RequestBody UpdateCategoryDto categoryDto, String modifiedBy) {
        modifiedBy = "admin";
        return categoryService.update(categoryDto, modifiedBy);
    }

    @DeleteMapping
    public ApiResponse<Boolean> delete(@RequestParam(value = "id", required = false) Long id) {
        return categoryService.delete(id);
    }
}
