package com.example.managerproduct.service.Impl;

import com.example.managerproduct.entity.ImageProduct;
import com.example.managerproduct.repository.ImageProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageProductService {

    @Autowired
    private ImageProductRepository imageProductRepository;

    public void saveImage(MultipartFile file) throws IOException {
        ImageProduct imageProduct = new ImageProduct();
        imageProduct.setName(file.getOriginalFilename());
        imageProduct.setData(file.getBytes());
        imageProductRepository.save(imageProduct);
    }
}