package com.example.managerproduct.service.Impl;

import com.example.managerproduct.entity.ImageProduct;
import com.example.managerproduct.repository.ImageProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ImageProductService {

    @Autowired
    private ImageProductRepository imageProductRepository;

    // Lấy tất cả các ảnh
    public Set<ImageProduct> getAllImages() {
        return new HashSet<>(imageProductRepository.findAll());
    }

    // Lấy ảnh theo ID
    public ImageProduct getImageById(Long id) {
        return imageProductRepository.findById(id).orElse(null);
    }

    public void saveImage(MultipartFile[] files) throws IOException {
        Set<ImageProduct> images = new HashSet<>();
        for (MultipartFile file : files) {
            try {
                ImageProduct imageProduct = ImageProduct.builder()
                        .name(file.getOriginalFilename())
                        .data(file.getBytes())
                        .build();
                images.add(imageProduct);
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}