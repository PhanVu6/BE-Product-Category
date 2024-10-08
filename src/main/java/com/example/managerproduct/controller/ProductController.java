package com.example.managerproduct.controller;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.repository.ProductRepository;
import com.example.managerproduct.service.Impl.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping
    public ApiResponse<Page<ProductDto>> getAllProduct(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "status", required = false) String status,
                                                       @RequestParam(value = "productCode", required = false) String productCode,
                                                       @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                       @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        productCode = (productCode == null || productCode.trim().isEmpty()) ? null : productCode;
        Pageable pageable = PageRequest.of(page, size);

        // Chuyển endDate thành LocalDateTime với giờ cuối ngày nếu có
        LocalDateTime endDateEndOfDay = null;
        if (endDate != null) {
            endDateEndOfDay = endDate.atTime(LocalTime.MAX); // 23:59:59
        }

        return productService.getAllProduct(name, status, productCode, startDate, endDateEndOfDay, pageable);
    }


    @GetMapping("{id}")
    public ApiResponse<ProductDto> getById(@PathVariable("id") Long id) {
        return productService.getById(id);
    }

    @PostMapping("img")
    public ApiResponse<ProductDto> create(@RequestPart("product") @Valid CreateProductDto productDto,
                                          @RequestPart(value = "files", required = false) MultipartFile[] multipartFiles,
                                          String createBy) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        @Valid CreateProductDto productDtoToJson = objectMapper.readValue(productDto, CreateProductDto.class);
        createBy = "admin";
        return productService.create(productDto, multipartFiles, createBy);
    }


    @PutMapping("img")
    public ApiResponse<ProductDto> update(@RequestPart("product") @Valid UpdateProductDto productDto,
                                          @RequestPart(value = "files", required = false) MultipartFile[] multipartFiles,
                                          String modifiedBy) throws JsonProcessingException {

        modifiedBy = "admin";
        return productService.update(productDto, multipartFiles, modifiedBy);
    }

    @DeleteMapping("hard/{id}")
    public ApiResponse<Boolean> delete(@PathVariable(value = "id", required = false) Long id) {
        return productService.delete(id);
    }

    @DeleteMapping("{id}")
    public ApiResponse<ProductDto> deleteMem(@PathVariable(value = "id", required = false) Long id) {
        return productService.deleteMem(id);
    }
}