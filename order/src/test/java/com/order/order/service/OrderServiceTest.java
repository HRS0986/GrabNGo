package com.order.order.service;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderResponse;
import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOrders() {
        // Arrange
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = Arrays.asList(order1, order2);
        OrderDTO orderDTO1 = new OrderDTO();
        OrderDTO orderDTO2 = new OrderDTO();

        when(orderRepository.findAll()).thenReturn(orders);
        when(modelMapper.map(order1, OrderDTO.class)).thenReturn(orderDTO1);
        when(modelMapper.map(order2, OrderDTO.class)).thenReturn(orderDTO2);

        // Act
        List<OrderDTO> result = orderService.getAllOrders();

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void testPlaceOrder() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        Order order = new Order();
        order.setUserId(1);
        Order savedOrder = new Order();
        savedOrder.setUserId(1);
        savedOrder.setStatus("PLACED");
        savedOrder.setCreatedDateTime(LocalDateTime.now());

        when(modelMapper.map(orderDTO, Order.class)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDTO.class)).thenReturn(orderDTO);

        // Act
        OrderDTO result = orderService.placeOrder(orderDTO);

        // Assert
        assertNotNull(result);
        verify(orderRepository).save(order);
    }

    @Test
    void testChangeOrderStatus_Success() {
        // Arrange
        int orderId = 1;
        String newStatus = "DELIVERED";
        Order order = new Order();
        order.setUserId(orderId);
        order.setStatus("PLACED");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        OrderDTO result = orderService.changeOrderStatus(orderId, newStatus);

        // Assert
        assertEquals(newStatus, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testChangeOrderStatus_OrderNotFound() {
        // Arrange
        int orderId = 999;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.changeOrderStatus(orderId, "SHIPPED"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testFilterOrders() {
        // Arrange
        Integer userId = 1;
        String status = "PLACED";
        Order order = new Order();
        List<Order> orders = List.of(order);
        OrderResponse orderResponse = new OrderResponse();

        when(orderRepository.findOrdersByCriteria(userId, status)).thenReturn(orders);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(orderResponse);

        // Act
        List<OrderResponse> result = orderService.filterOrders(userId, status);

        // Assert
        assertEquals(1, result.size());
        verify(orderRepository).findOrdersByCriteria(userId, status);
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        int orderId = 1;
        Order order = new Order();
        order.setUserId(orderId);
        order.setStatus("PLACED");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        OrderResponse orderResponse = new OrderResponse();
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.cancelOrder(orderId);

        // Assert
        assertEquals("Cancelled", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder_AlreadyCancelled() {
        // Arrange
        int orderId = 1;
        Order order = new Order();
        order.setUserId(orderId);
        order.setStatus("Cancelled");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(orderId));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        // Arrange
        int orderId = 999;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(orderId));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        int orderId = 1;
        Order order = new Order();
        order.setUserId(orderId);
        OrderDTO orderDTO = new OrderDTO();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Act
        OrderDTO result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        int orderId = 999;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        OrderDTO result = orderService.getOrderById(orderId);

        // Assert
        assertNull(result);
        verify(orderRepository).findById(orderId);
    }
}
