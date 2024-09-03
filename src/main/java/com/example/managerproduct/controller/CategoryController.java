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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("category")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<Page<CategoryDto>> getAllProduct(@RequestParam(value = "name", required = false) String name,
                                                        @RequestParam(value = "categoryCode", required = false) String categoryCode,
                                                        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                        @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        categoryCode = (categoryCode == null || categoryCode.trim().isEmpty()) ? null : categoryCode;

        Pageable pageable = PageRequest.of(page, size);
        return categoryService.getAllCategory(name, categoryCode, startDate, endDate, pageable);
    }

    @GetMapping("{id}")
    public ApiResponse<CategoryDto> getById(@PathVariable("id") Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("open")
    public ApiResponse<List<CategoryDto>> open(@RequestParam(value = "name", required = false) String name) {
        return categoryService.open(name);
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

    @DeleteMapping("{id}")
    public ApiResponse<Boolean> delete(@PathVariable(value = "id", required = false) Long id) {
        return categoryService.delete(id);
    }
}
