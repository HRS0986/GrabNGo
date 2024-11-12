package com.order.order.controller;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderResponse;
import com.order.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    // Constructor-based dependency injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO placedOrder = orderService.placeOrder(orderDTO);
        return ResponseEntity.ok(orderDTO);
    }
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderDTO> changeOrderStatus(@PathVariable int orderId, @RequestBody String newStatus) {
        OrderDTO updatedOrder = orderService.changeOrderStatus(orderId, newStatus);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
    @GetMapping("/orders/filter")
    public ResponseEntity<List<OrderResponse>> filterOrders(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status) {
        List<OrderResponse> orders = orderService.filterOrders(userId, status);
        return ResponseEntity.ok(orders);
    }
    @PutMapping("/{orderId}")
    public OrderResponse cancelOrder(@PathVariable int orderId) {
        return orderService.cancelOrder(orderId);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetails(@PathVariable int orderId) {  // Changed to Long if orderId is Long
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        if (orderDTO != null) {
            return ResponseEntity.ok(orderDTO);  // Return 200 with the order details if found
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if the order is not found
        }
    }
}












