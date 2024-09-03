package com.example.managerproduct.repository;


import com.example.managerproduct.entity.ImageProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {
    List<ImageProduct> findByProductId(Long id);
}