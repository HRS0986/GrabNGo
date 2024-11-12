package com.order.order.service;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderResponse;
import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.order.order.config.ModelMapperConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDTO placeOrder(OrderDTO orderDTO) {
        Order order = modelMapper.map(orderDTO, Order.class); // Map OrderDTO to Order
        order.setCreatedDateTime(LocalDateTime.now());
        order.setStatus("PLACED");
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class); // Map saved Order to OrderDTO
    }

    public OrderDTO changeOrderStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")); // Handle the case when order is not found

        order.setStatus(newStatus); // Set the new status
        Order updatedOrder = orderRepository.save(order); // Save the updated order

        // Return the updated order as OrderDTO
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }


    public List<OrderResponse> filterOrders(Integer userId, String status) {
        List<Order> filteredOrders = orderRepository.findOrdersByCriteria(userId, status);
        return filteredOrders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(int orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if ("Cancelled".equals(order.getStatus())) {
                throw new RuntimeException("Order is already cancelled.");
            }
            order.setStatus("Cancelled");
            orderRepository.save(order);
            return modelMapper.map(order, OrderResponse.class);
        } else {
            throw new RuntimeException("Order not found.");
        }
    }

    public OrderDTO getOrderById(int orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return modelMapper.map(order, OrderDTO.class);
        } else {
            return null;
        }
    }




}
