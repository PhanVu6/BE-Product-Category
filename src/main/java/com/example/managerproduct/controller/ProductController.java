package com.example.managerproduct.controller;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.service.Impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductDto>> getAllProduct(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "productCode", required = false) String productCode,
                                                       @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        productCode = (productCode == null || productCode.trim().isEmpty()) ? null : productCode;
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProduct(name, productCode, startDate, endDate, pageable);
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

    @PostMapping("img")
    public ApiResponse<ProductDto> create(@RequestParam("product") String productDto,
                                          @RequestParam("files") MultipartFile[] multipartFiles,
                                          String createBy) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateProductDto productDtoToJson = objectMapper.readValue(productDto, CreateProductDto.class);
        createBy = "admin";
        return productService.create(productDtoToJson, multipartFiles, createBy);
    }


    @PutMapping("img")
    public ApiResponse<ProductDto> update(@RequestParam("product") String productDto,
                                          @RequestParam("files") MultipartFile[] multipartFiles,
                                          String modifiedBy) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UpdateProductDto productDtoToJson = objectMapper.readValue(productDto, UpdateProductDto.class);
        modifiedBy = "admin";
        return productService.update(productDtoToJson, multipartFiles, modifiedBy);
    }

    @DeleteMapping("{id}")
    public ApiResponse<Boolean> delete(@PathVariable(value = "id", required = false) Long id) {
        return productService.delete(id);
    }
}