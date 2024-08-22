package com.example.managerproduct.repository;

import com.example.managerproduct.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "from Product p " +
            "left join fetch p.productCategories pc " +
            "left join fetch pc.category c " +
            "where (:name is null or p.name like %:name%) " +
            "and (pc.status is null or pc.status = '1') " +
            "order by p.id")
    Page<Product> getAll(@Param("name") String name,
                         Pageable pageable);
}
