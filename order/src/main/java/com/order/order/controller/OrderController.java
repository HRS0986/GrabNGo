package com.order.order.controller;

import com.order.order.dto.OrderDTO;
import com.order.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

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
        return new ResponseEntity<>(placedOrder, HttpStatus.CREATED);

    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderDTO> changeOrderStatus(@PathVariable int orderId, @RequestBody String newStatus) {
        OrderDTO updatedOrder = orderService.changeOrderStatus(orderId, newStatus);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<OrderDTO>> filterOrders(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status) {
        List<OrderDTO> orders = orderService.filterOrders(userId, status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}")
    public OrderDTO cancelOrder(@PathVariable int orderId) {
        return orderService.cancelOrder(orderId);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetails(@PathVariable int orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);

        if (orderDTO != null) {
            return ResponseEntity.ok(orderDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}












