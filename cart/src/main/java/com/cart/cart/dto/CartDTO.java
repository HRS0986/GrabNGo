package com.cart.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private int cartId;
    private int userId;
    private int totalAmount;
    private double totalPrice;
    private boolean isActive;
}
