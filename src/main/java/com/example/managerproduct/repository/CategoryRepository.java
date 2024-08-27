package com.example.managerproduct.repository;

import com.example.managerproduct.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select distinct c " +
            "from Category c " +
            "where (:str is null or c.name like %:str%) " +
            "or c.category_code like %:str% ")
    Page<Category> getAll(@Param("str") String str,
                          Pageable pageable);

    @Query(value = "select distinct c " +
            "from Category c " +
            "where (:str is null or c.name like %:str%) " +
            "or c.category_code like %:str% ")
    List<Category> open(@Param("str") String str);


}
