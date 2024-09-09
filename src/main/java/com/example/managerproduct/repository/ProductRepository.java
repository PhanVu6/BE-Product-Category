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
            "and (:productCode is null or p.productCode like %:productCode%) " +
            "and (:startDate is null or function('date', p.createdDate) >= :startDate) " +
            "and (:endDate is null or function('date', p.createdDate) <= :endDate) " +
            "order by p.createdDate desc ",
            countQuery = "select count(distinct p) " +
                    "from Product p " +
                    "where (:name is null or p.name like %:name%) " +
                    "and (:productCode is null or p.productCode like %:productCode%) " +
                    "and (:startDate is null or function('date', p.createdDate) >= :startDate) " +
                    "and (:endDate is null or function('date', p.createdDate) <= :endDate)")
    Page<Product> getAll(@Param("name") String name,
                         @Param("productCode") String productCode,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         Pageable pageable);

    @Query(value = "select p.*, GROUP_CONCAT(DISTINCT c.name SEPARATOR ', ') AS category " +
            "from product p " +
            "left join productcategory pc on pc.product_id = p.id and pc.status = 'AVAILABLE' " +
            "left join category c on c.id = pc.category_id and c.status = 'AVAILABLE' " +
            "where p.status = :status " +
            "and (:name is null or p.name like concat('%', :name, '%')) " +
            "and (:productCode is null or p.product_code like concat('%', :productCode, '%')) " +
            "and (:startDate is null or p.created_date >= :startDate) " +
            "and (:endDate is null or p.created_date <= :endDate) " +
            "group by p.id " +
            "order by p.created_date",
            countQuery = "select count(distinct p.id) " +
                    "from product p " +
                    "where p.status = :status " +
                    "and (:name is null or p.name like concat('%', :name, '%')) " +
                    "and (:productCode is null or p.product_code like concat('%', :productCode, '%')) " +
                    "and (:startDate is null or p.created_date >= :startDate) " +
                    "and (:endDate is null or p.created_date <= :endDate) ",
            nativeQuery = true)
    Page<Object[]> searchAll(@Param("name") String name,
                             @Param("status") String status,
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
            "order by p.createdDate desc ")
    List<Product> getAll();

    @Query(value = "select distinct p " +
            "from Product p " +
            "left join fetch p.productCategories pc " +
            "left join fetch pc.category c " +
            "left join fetch c.imageCategories ic " +
            "where p.status = 'AVAILABLE' " +
            "and (:str is null or p.name like %:str% or p.productCode like %:str%) " +
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


    @Query(value = "SELECT p.id AS id, p.name AS productName, p.product_code AS productCode, " +
            "GROUP_CONCAT(c.name ORDER BY c.name SEPARATOR ', ') AS categoryName, " +
            "pc.status AS status, p.created_date AS createDate, p.modified_date AS modifiedDate, " +
            "p.quantity AS quantity, p.description AS description, p.price AS price " +
            "FROM product p " +
            "JOIN productcategory pc ON p.id = pc.product_id " +
            "JOIN category c ON pc.category_id = c.id " +
            "GROUP BY p.id, p.name, p.product_code, pc.status, p.created_date, p.modified_date, " +
            "p.quantity, p.description, p.price " +
            "ORDER BY p.created_date DESC", // Thêm điều kiện sắp xếp theo ngày tạo
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM product p " +
                    "JOIN product_category pc ON p.id = pc.product_id " +
                    "JOIN category c ON pc.category_id = c.id",
            nativeQuery = true)
    Page<Object[]> findProductDetailsWithCategories(Pageable pageable);


    // Kiểm tra xem productCode có tồn tại hay không
    boolean existsByProductCode(String productCode);

    // Nếu cần kiểm tra theo productId để tránh kiểm tra chính sản phẩm đó khi cập nhật
    boolean existsByProductCodeAndIdNot(String productCode, Long id);
}
