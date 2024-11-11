package com.order.order.dto;

import com.order.order.model.Order;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemDTO {
    private int productId;
    private int quantity;
    private Double sellPrice;
}
