package com.cart.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private int totalAmount;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CartItem> cartItems;
}

