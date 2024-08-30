package com.example.managerproduct.controller;

import com.example.managerproduct.entity.Category;
import com.example.managerproduct.entity.Product;
import com.example.managerproduct.repository.CategoryRepository;
import com.example.managerproduct.repository.ProductRepository;
import com.example.managerproduct.service.Impl.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/categories/download")
    public ResponseEntity<byte[]> downloadCategoriesExcel() throws IOException {
        List<Category> categories = categoryRepository.findAll();

        ByteArrayInputStream in = excelExportService.exportCategoriesToExcel(categories);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=categories.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(in.readAllBytes());
    }

    @GetMapping("/products/download")
    public ResponseEntity<byte[]> downloadProductsExcel() throws IOException {
        List<Product> products = productRepository.findAll();

        ByteArrayInputStream in = excelExportService.exportProductsToExcel(products);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(in.readAllBytes());
    }
}
