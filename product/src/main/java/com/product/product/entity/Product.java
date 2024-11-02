package com.product.product.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "product_id")  // Specifies the column name for productId
    private int productId;

    @Column(nullable = false)
    private String productName;

//    @Column(name = "product_description")
    private String productDescription;

    @Column(nullable = false)
    private double productPrice;

    @Column(nullable = false)
    private int productQuantity;


    private String productImageUrl;

    @Column(nullable = false)
    private int productCategoryId;

    @Column(nullable = false)
    private boolean isAvailable ;

    @Column(nullable = false)
    private boolean isActive ;
}
