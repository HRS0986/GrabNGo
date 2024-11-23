package com.cart.cart.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {
    private int cartId;
    private int userId;
    private int totalAmount = 0;
    private double totalPrice = 0.00;
    private boolean isActive = true;
}
