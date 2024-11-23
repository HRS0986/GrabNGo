package com.order.order.service;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderItemDTO;
import com.order.order.model.Order;
import com.order.order.model.OrderItem;
import com.order.order.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final WebClient.Builder webClientBuilder;

    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.webClientBuilder = webClientBuilder;
    }


    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    public OrderDTO placeOrder(OrderDTO orderDTO) {
        int userId = orderDTO.getUserId();

        Integer cartId=webClientBuilder.build()
                .get()
                .uri("/api/v1/cart/user/"+userId)
                .retrieve()
                .bodyToMono(Integer.class)
                .block();

        if (cartId == null) {
            throw new RuntimeException("No cart found for user");
        }

        List<OrderItemDTO> cartItems = webClientBuilder.build()
                .get()
                .uri("/api/v1/cart/" + cartId + "/items") // Replace with endpoint to fetch cart items
                .retrieve()
                .bodyToFlux(OrderItemDTO.class)
                .collectList()
                .block();

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("No items found in the cart");
        }

        Order order = modelMapper.map(orderDTO, Order.class);
        List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                .map(orderItemDTO -> {
                    OrderItem orderItem = modelMapper.map(orderItemDTO, OrderItem.class);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    //change order status
    public OrderDTO changeOrderStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")); // Handle the case when order is not found

        order.setStatus(newStatus); // Set the new status
        Order updatedOrder = orderRepository.save(order); // Save the updated order

        // Return the updated order as OrderDTO
        return modelMapper.map(updatedOrder, OrderDTO.class);
    }

    public List<OrderDTO> filterOrders(Integer userId, String status) {
        List<Order> filteredOrders = orderRepository.findOrdersByCriteria(userId, status);
        return filteredOrders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))  // Mapping to OrderDTO
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO cancelOrder(int orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if ("Cancelled".equals(order.getStatus())) {
                throw new RuntimeException("Order is already cancelled.");
            }
            order.setStatus("Cancelled");
            orderRepository.save(order);
            return modelMapper.map(order, OrderDTO.class);
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
