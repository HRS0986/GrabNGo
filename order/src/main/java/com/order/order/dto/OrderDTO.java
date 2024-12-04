package com.order.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private int orderId;
    private int userId;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdDateTime;
    private Double discount;

    private List<OrderItemDTO> orderItems;
}
