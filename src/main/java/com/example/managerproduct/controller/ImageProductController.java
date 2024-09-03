package com.example.managerproduct.controller;

import com.example.managerproduct.service.Impl.ImageProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageProductController {

    @Autowired
    private ImageProductService imageProductService;

}