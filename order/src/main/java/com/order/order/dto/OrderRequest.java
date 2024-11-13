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
public class OrderRequest {
    private List<OrderItemDTO> orderItems;
    private int userId;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdDateTime;
    private Double discount;
}
