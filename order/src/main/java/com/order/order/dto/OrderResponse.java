package com.order.order.dto;

import com.order.order.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponse {
    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private String status;
    private Double totalPrice;
    private List<OrderItem> orderItems;
}
