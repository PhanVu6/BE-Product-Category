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
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "select distinct c " +
            "from Category c " +
            "left join fetch c.imageCategories i " +
            "where (c.status = :status) " +
            "and (:name is null or c.name like %:name%) " +
            "and (:categoryCode is null or c.categoryCode like %:categoryCode%) " +
            "and (:startDate is null or function('date', c.createdDate) >= :startDate) " +
            "and (:endDate is null or function('date', c.createdDate) <= :endDate) " +
            "order by c.createdDate desc ")
    Page<Category> getAll(@Param("name") String name,
                          @Param("status") String status,
                          @Param("categoryCode") String categoryCode,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          Pageable pageable);


    @Query(value = "select c.* " +
            "from category c " +
            "left join productcategory pc on pc.product_id = c.id " +
            "left join product p on p.id = pc.category_id " +
            "where (c.status = :status) " +
            "and (:name is null or c.name like concat('%', :name, '%')) " +
            "and (:productCode is null or c.category_code like concat('%', :productCode, '%')) " +
            "and (:startDate is null or c.created_date >= :startDate) " +
            "and (:endDate is null or c.created_date <= :endDate) " +
            "group by c.id " +
            "order by c.created_date",
            countQuery = "select count(distinct p.id) " +
                    "from category c " +
                    "where c.status = :status " +
                    "and (:name is null or c.name like concat('%', :name, '%')) " +
                    "and (:productCode is null or c.category_code like concat('%', :productCode, '%')) " +
                    "and (:startDate is null or c.created_date >= :startDate) " +
                    "and (:endDate is null or c.created_date <= :endDate) ",
            nativeQuery = true)
    Page<Object[]> searchAll(@Param("name") String name,
                             @Param("status") String status,
                             @Param("productCode") String productCode,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate,
                             Pageable pageable);

    @Query(value = "select distinct c " +
            "from Category c " +
            "left join fetch c.imageCategories i " +
            "where (:str is null or c.name like %:str%) " +
            "or c.categoryCode like %:str% ")
    List<Category> open(@Param("str") String str);

    @Query(value = "from Category c " +
            "left join fetch c.imageCategories i " +
            "where c.id = :id ")
    Category getById(@Param("id") Long id);

    // Kiểm tra xem categoryCode có tồn tại hay không
    boolean existsByCategoryCode(String categoryCode);

    // Nếu cần kiểm tra theo categoryId để tránh kiểm tra chính danh mục đó khi cập nhật
    boolean existsByCategoryCodeAndIdNot(String categoryCode, Long id);

    // Phương thức để kiểm tra tồn tại của danh sách categoryCode
    @Query("SELECT c.categoryCode FROM Category c WHERE c.categoryCode IN :categoryCodes ")
    List<String> findExistingCategoryCodes(@Param("categoryCodes") Set<String> categoryCodes);

}
