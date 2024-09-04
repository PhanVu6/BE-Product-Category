package com.example.managerproduct.repository;

import com.example.managerproduct.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "select distinct p " +
            "from Product p " +
            "left join fetch p.imageProducts i " +
            "left join fetch p.productCategories pc " +
            "left join fetch pc.category c " +
            "left join fetch c.imageCategories ic " +
            "where (:name is null or p.name like %:name%) " +
            "and (:productCode is null or p.product_code like %:productCode%) " +
            "and (:startDate is null or function('date', p.createdDate) >= :startDate) " +
            "and (:endDate is null or function('date', p.createdDate) <= :endDate) " +
            "order by function('date', p.createdDate) ")
    Page<Product> getAll(@Param("name") String name,
                         @Param("productCode") String productCode,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         Pageable pageable);

    @Query(value = "select distinct p " +
            "from Product p " +
            "left join fetch p.imageProducts i " +
            "left join fetch p.productCategories pc " +
            "left join fetch pc.category c " +
            "left join fetch c.imageCategories ic " +
            "order by function('date', p.createdDate) ")
    List<Product> getAll();

    @Query(value = "select distinct p " +
            "from Product p " +
            "left join fetch p.productCategories pc " +
            "left join fetch pc.category c " +
            "left join fetch c.imageCategories ic " +
            "where p.status = 'AVAILABLE' " +
            "and (:str is null or p.name like %:str% or p.product_code like %:str%) " +
            "order by p.id")
    Page<Product> open(@Param("str") String str,
                       Pageable pageable);

    @Query(value = "from Product p " +
            "left join fetch p.imageProducts i " +
            "left join fetch p.productCategories pc " +
            "left join fetch pc.category c " +
            "left join fetch c.imageCategories ic " +
            "where p.id = :id ")
    Product getById(@Param("id") Long id);
}
