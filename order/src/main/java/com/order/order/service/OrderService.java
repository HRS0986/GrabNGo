package com.order.order.service;

import com.order.order.dto.CartItemDTO;
import com.order.order.dto.OrderRequest;
import com.order.order.dto.OrderResponse;
import com.order.order.model.Order;
import com.order.order.model.OrderItem;
import com.order.order.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;


@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }


    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // Map OrderRequest to Order entity
        Order order = modelMapper.map(orderRequest, Order.class);

        // Set status and created date
        order.setStatus("Created");
        order.setCreatedDateTime(LocalDateTime.now());

        // Map the orderItems from the request
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(item -> modelMapper.map(item, OrderItem.class))
                .collect(Collectors.toList());

        // Set the items to the order
        order.setOrderItems(orderItems);

        // Save the order in the database
        orderRepository.save(order);

        // Map the saved order to OrderResponse
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);

        // Set the status and order date in the response
        orderResponse.setStatus("Created");
        orderResponse.setOrderDate(order.getCreatedDateTime());

        return orderResponse;
    }

    public Order placeOrder(String customerName, List<CartItemDTO> orderItemDTO) {
        Order order = new Order();
        order.setStatus("Created");
        order.setCreatedDateTime(LocalDateTime.now());

        // Map the list of OrderItemDTOs to OrderItem entities
        List<OrderItem> orderItems = orderItemDTO.stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setProductId(dto.getProductId()); // Corrected: use dto
            item.setQuantity(dto.getQuantity());   // Corrected: use dto
            item.setPrice(dto.getPrice());         // Corrected: use dto
            item.setOrder(order);                  // Set order reference in item
            return item;
        }).collect(Collectors.toList());

        // Set the list of OrderItems to the Order
        order.setOrderItems(orderItems);

        // Save the order and associated order items to the repository
        return orderRepository.save(order);
    }


    public List<OrderResponse> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(int orderId, String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status); // Update the status
            orderRepository.save(order); // Save the updated order
            return modelMapper.map(order, OrderResponse.class); // Return the updated order response
        } else {
            throw new RuntimeException("Order not found"); // Handle not found case
        }
    }
    public List<OrderResponse> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status); // Query orders by status
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    // Method to cancel an order
    @Transactional
    public OrderResponse cancelOrder(int orderId) {
        // Find the order by its ID
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Check if the order is already canceled or completed
            if ("Cancelled".equals(order.getStatus())) {
                throw new RuntimeException("Order is already cancelled.");
            }

            // Update the status to "Cancelled"
            order.setStatus("Cancelled");

            // Save the updated order
            orderRepository.save(order);

            // Map the saved order to OrderResponse and return it
            return modelMapper.map(order, OrderResponse.class);
        } else {
            throw new RuntimeException("Order not found.");
        }
    }




}


