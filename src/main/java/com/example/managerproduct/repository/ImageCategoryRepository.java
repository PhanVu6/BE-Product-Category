package com.example.managerproduct.repository;


import com.example.managerproduct.entity.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageCategoryRepository extends JpaRepository<ImageCategory, Long> {
    List<ImageCategory> findByCategoryId(Long id);
}