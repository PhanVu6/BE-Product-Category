package com.example.managerproduct.controller;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.service.Impl.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductDto>> getAllProduct(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProduct(name, pageable);
    }

    @PostMapping
    public ApiResponse<ProductDto> create(@RequestBody CreateProductDto productDto, String createBy) {
        createBy = "admin";
        return productService.create(productDto, createBy);
    }

    @PutMapping
    public ApiResponse<ProductDto> update(@RequestBody UpdateProductDto productDto, String modifiedBy) {
        modifiedBy = "admin";
        return productService.update(productDto, modifiedBy);
    }

    @DeleteMapping
    public ApiResponse<Boolean> delete(@RequestParam(value = "id", required = false) Long id) {
        return productService.delete(id);
    }
}