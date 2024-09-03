package com.example.managerproduct.repository;

import com.example.managerproduct.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select distinct c " +
            "from Category c " +
            "where (:name is null or c.name like %:name%) " +
            "and (:categoryCode is null or c.category_code like %:categoryCode%) " +
            "and (:startDate is null or function('date', c.createdDate) >= :startDate) " +
            "and (:endDate is null or function('date', c.createdDate) <= :endDate) " +
            "order by function('date', c.createdDate) ")
    Page<Category> getAll(@Param("name") String name,
                          @Param("categoryCode") String categoryCode,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          Pageable pageable);

    @Query(value = "select distinct c " +
            "from Category c " +
            "where (:str is null or c.name like %:str%) " +
            "or c.category_code like %:str% ")
    List<Category> open(@Param("str") String str);


}
