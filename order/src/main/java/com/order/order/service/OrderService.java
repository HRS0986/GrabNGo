package com.order.order.service;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderRequest;
import com.order.order.dto.OrderResponse;
import com.order.order.mapper.OrderMapper;
import com.order.order.model.Order;
import com.order.order.model.OrderItem;
import com.order.order.repository.OrderItemRepository;
import com.order.order.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


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


    public OrderDTO placeOrder(OrderDTO orderDTO) {
        // Convert DTO to entity
        Order order = OrderMapper.toOrderEntity(orderDTO);

        // Set default values for the order
        order.setCreatedDateTime(LocalDateTime.now());
        order.setStatus("PLACED");

        // Save the order entity
        Order savedOrder = orderRepository.save(order);

        // Return the saved order as DTO
        return OrderMapper.toOrderDTO(savedOrder);
    }

    public List<OrderResponse> filterOrders(Integer userId, String status) {
        List<Order> filteredOrders = orderRepository.findOrdersByCriteria(userId, status);
        // Convert `Order` entities to `OrderResponse` and return
        return filteredOrders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }
   
    public OrderDTO getOrderById(int orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        // If the order is not found, return null or throw an exception as per your requirement
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return OrderMapper.toOrderDTO(order);  // Convert the Order entity to OrderDTO
        } else {
            // Return null or handle the case where the order is not found
            return null;
        }
    }

//    public List<OrderResponse> getOrdersByUserId(int userId) {
//        List<Order> orders = orderRepository.findByUserId(userId);
//        return orders.stream()
//                .map(order -> modelMapper.map(order, OrderResponse.class))
//                .collect(Collectors.toList());
//    }

//    public List<OrderResponse> getOrdersByStatus(String status) {
//        List<Order> orders = orderRepository.findByStatus(status); // Query orders by status
//        return orders.stream()
//                .map(order -> modelMapper.map(order, OrderResponse.class))
//                .collect(Collectors.toList());
//    }

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


