package com.order.order.controller;

import com.order.order.dto.OrderRequest;
import com.order.order.dto.OrderResponse;
import com.order.order.model.Order;
import com.order.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;



    @GetMapping ("/getOrders")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }
    @GetMapping("/getOrders/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable int userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/updateStatus/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable int orderId, @RequestBody String status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }
    @GetMapping("/getOrdersByStatus/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    @PutMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(@PathVariable int orderId) {
        return orderService.cancelOrder(orderId);
    }


}
