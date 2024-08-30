package com.example.managerproduct.controller;

import com.example.managerproduct.entity.ImageProduct;
import com.example.managerproduct.repository.ImageProductRepository;
import com.example.managerproduct.service.Impl.ImageProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
public class ImageProductController {

    @Autowired
    private ImageProductService imageProductService;
    @Autowired
    private ImageProductRepository imageProductRepository;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<ImageProduct> imageOptional = imageProductRepository.findById(id);

        if (imageOptional.isPresent()) {
            ImageProduct imageProduct = imageOptional.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageProduct.getData());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            imageProductService.saveImage(file);
            return ResponseEntity.status(HttpStatus.OK).body("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }
}