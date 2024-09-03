package com.example.managerproduct.service.Impl;

import com.example.managerproduct.repository.ImageProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageProductService {

    @Autowired
    private ImageProductRepository imageProductRepository;
}