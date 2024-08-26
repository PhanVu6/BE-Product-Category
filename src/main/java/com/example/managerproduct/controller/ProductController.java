package com.example.managerproduct.controller;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.service.Impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductDto>> getAllProduct(@RequestParam(value = "search", required = false) String search,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProduct(search, pageable);
    }

    @GetMapping("{id}")
    public ApiResponse<ProductDto> getById(@PathVariable("id") Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public ApiResponse<ProductDto> create(@RequestBody @Valid CreateProductDto productDto, String createBy) {
        createBy = "admin";
        return productService.create(productDto, createBy);
    }

    @PutMapping
    public ApiResponse<ProductDto> update(@RequestBody @Valid UpdateProductDto productDto, String modifiedBy) {
        modifiedBy = "admin";
        return productService.update(productDto, modifiedBy);
    }

    @DeleteMapping("{id}")
    public ApiResponse<Boolean> delete(@PathVariable(value = "id", required = false) Long id) {
        return productService.delete(id);
    }
}