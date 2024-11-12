package com.order.order.mapper;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderItemDTO;
import com.order.order.model.Order;
import com.order.order.model.OrderItem;

import java.util.stream.Collectors;

public class OrderMapper {

    // Convert from Order entity to OrderDTO
    public static OrderDTO toOrderDTO(Order order) {
        return new OrderDTO(
                order.getOrderId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedDateTime(),
                order.getDiscount(),
                order.getOrderItems().stream().map(OrderMapper::toOrderItemDTO).collect(Collectors.toList())
        );
    }

    // Convert from OrderDTO to Order entity
    public static Order toOrderEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setUserId(orderDTO.getUserId());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatus(orderDTO.getStatus());
        order.setCreatedDateTime(orderDTO.getCreatedDateTime());
        order.setDiscount(orderDTO.getDiscount());
        order.setOrderItems(orderDTO.getOrderItems().stream().map(OrderMapper::toOrderItemEntity).collect(Collectors.toList()));
        return order;
    }

    // Convert from OrderItem entity to OrderItemDTO
    public static OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getSellPrice()
        );
    }

    // Convert from OrderItemDTO to OrderItem entity
    public static OrderItem toOrderItemEntity(OrderItemDTO orderItemDTO) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(orderItemDTO.getProductId());
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setSellPrice(orderItemDTO.getSellPrice());
        return orderItem;
    }
}
