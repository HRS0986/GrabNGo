package com.product.product.repository;
import com.product.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isActive = false WHERE p.categoryId = ?1")  // Use correct field name
    void softDeleteByCategoryId(int categoryId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isActive = true WHERE p.categoryId = ?1")  // Use correct field name
    void restoreByCategoryId(int categoryId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isActive = false WHERE p.productId = ?1")  // Assuming 'isActive' is a field in Product
    void softDeleteById(int id);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isActive = true WHERE p.productId = ?1")  // Assuming 'isActive' is a field in Product
    void restoreProductById(int id);


}
