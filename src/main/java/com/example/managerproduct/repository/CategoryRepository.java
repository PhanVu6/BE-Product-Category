package com.example.managerproduct.repository;

import com.example.managerproduct.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "from Category c " +
            "where (:name is null or c.name like %:name%) ")
    Page<Category> getAll(@Param("name") String name,
                          Pageable pageable);
}
