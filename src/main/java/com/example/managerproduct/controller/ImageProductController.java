package com.example.managerproduct.controller;

import com.example.managerproduct.entity.ImageProduct;
import com.example.managerproduct.repository.ImageProductRepository;
import com.example.managerproduct.service.Impl.ImageProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
public class ImageProductController {

    @Autowired
    private ImageProductService imageProductService;
    @Autowired
    private ImageProductRepository imageProductRepository;

    @GetMapping
    public ResponseEntity<Set<ImageProduct>> getAllImages() {
        Set<ImageProduct> images = imageProductService.getAllImages();
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    // Lấy ảnh theo ID
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        ImageProduct image = imageProductService.getImageById(id);

        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/jpeg"); // Hoặc image/png tùy loại file

        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("files") MultipartFile[] files) {
        try {
            imageProductService.saveImage(files);
            return ResponseEntity.status(HttpStatus.OK).body("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }
}