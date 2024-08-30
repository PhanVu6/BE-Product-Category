package com.example.managerproduct.repository;


import com.example.managerproduct.entity.ImageProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {
}