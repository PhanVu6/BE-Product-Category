package com.example.managerproduct.repository;

import com.example.managerproduct.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    @Query(
            value = "from ProductCategory pc " +
                    "join fetch pc.product p " +
                    "join fetch pc.category c " +
                    "where p.id = :productId"
    )
    List<ProductCategory> findByProductId(@Param("productId") Long studentId);

    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.status = :status WHERE pc.product.id = :productId AND pc.category.id IN :categoryIds")
    void changeStatusByProductAndCategories(@Param("productId") Long productId,
                                            @Param("categoryIds") Set<Long> categoryIds,
                                            @Param("status") String status);
}
