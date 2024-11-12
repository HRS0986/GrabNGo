package com.order.order.controller;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderRequest;
import com.order.order.dto.OrderResponse;
import com.order.order.model.Order;
import com.order.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping ("/orders")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }
    @GetMapping("/orderDetails")
    public ResponseEntity<OrderDTO> getOrderDetails(@PathVariable int orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        if (orderDTO != null) {
            return ResponseEntity.ok(orderDTO);  // Return the order details if found
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if order not found
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO placedOrder = orderService.placeOrder(orderDTO);
        return ResponseEntity.ok(placedOrder);
    }

    @PutMapping("/changeStatus")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable int orderId, @RequestBody String status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/orders/filter")
    public ResponseEntity<List<OrderResponse>> filterOrders(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status) {
        List<OrderResponse> orders = orderService.filterOrders(userId, status);
        return ResponseEntity.ok(orders);
    }

//    @GetMapping("/orders/{userId}")
//    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable int userId) {
//        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
//        return ResponseEntity.ok(orders);
//    }
//
//    @GetMapping("/getOrdersByStatus/{status}")
//    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
//        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
//        return ResponseEntity.ok(orders);
//    }

    @PutMapping("/cancel")
    public OrderResponse cancelOrder(@PathVariable int orderId) {
        return orderService.cancelOrder(orderId);
    }

}
