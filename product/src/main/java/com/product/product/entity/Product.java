//package com.product.product.entity;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Column;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "products")  // Specifies the table name in the database
//public class Product {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "product_id")  // Specifies the column name for productId
//    private int productId;
//
//    @Column(name = "product_name", nullable = false)
//    private String productName;
//
//    @Column(name = "product_description")
//    private String productDescription;
//
//    @Column(name = "product_price", nullable = false)
//    private double productPrice;
//
//    @Column(name = "product_quantity", nullable=false)
//    private int productQuantity;
//
//    @Column(name = "image_url")
//    private String productImageUrl;
//
//    @Column(name = "category_id", nullable = false)
//    private int productCategoryId;
//
//    @Column(name = "is_available", nullable=false)
//    private boolean isAvailable;
//
//    @Column(name = "is_active", nullable=false)
//    private boolean isActive;
//}

package com.product.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")  // Specifies the table name in the database
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")  // Specifies the column name for productId
    private int productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_price", nullable = false)
    private double productPrice;

    @Column(name = "product_quantity", nullable = false)
    private int productQuantity;

    @Column(name = "image_url")
    private String productImageUrl;

    @Column(name = "category_id", nullable = false)
    private int productCategoryId;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable ;

    @Column(name = "is_active", nullable = false)
    private boolean isActive ;
}
