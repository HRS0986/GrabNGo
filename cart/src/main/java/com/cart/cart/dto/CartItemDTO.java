package com.cart.cart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDTO {
    private int cartItemId;
    private int productId;
    private int quantity;
    private double price;
    private int cartId;
}
