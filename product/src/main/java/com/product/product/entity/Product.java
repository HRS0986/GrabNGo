package com.product.product.entity;

import jakarta.persistence.*;
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
    private int productId;

    @Column(nullable = false)
    private String productName;

    private String productDescription;

    @Column(nullable = false)
    private double productPrice;

    @Column(nullable = false)
    private int productQuantity;

    private String imageUrl;

    @Column(nullable = false)
    private int categoryId;

    @Column(nullable = false)
    private boolean isAvailable;

    @Column(nullable = false)
    private boolean isActive;
}
