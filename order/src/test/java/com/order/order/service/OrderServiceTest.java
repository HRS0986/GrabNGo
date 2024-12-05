package com.order.order.service;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderResponse;
import com.order.order.model.Order;
import com.order.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService; // The service we are testing

    @Mock
    private OrderRepository orderRepository; // Mocked dependency

    @Mock
    private ModelMapper modelMapper; // Mocked dependency

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        // Create test objects
        order = new Order();
        order.setUserId(1);
        order.setStatus("PLACED");
        order.setCreatedDateTime(LocalDateTime.now());

        orderDTO = new OrderDTO();
        orderDTO.setOrderId(1);
        orderDTO.setStatus("PLACED");
    }

    @Test
    void testGetAllOrders() {
        // Mock behavior
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Call the method
        List<OrderDTO> result = orderService.getAllOrders();

        // Verify results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PLACED", result.get(0).getStatus());

        // Verify interactions
        verify(orderRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(order, OrderDTO.class);
    }

    @Test
    void testPlaceOrder() {
        // Mock behavior
        when(modelMapper.map(orderDTO, Order.class)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Call the method
        OrderDTO result = orderService.placeOrder(orderDTO);

        // Verify results
        assertNotNull(result);
        assertEquals("PLACED", result.getStatus());

        // Verify interactions
        verify(orderRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(orderDTO, Order.class);
        verify(modelMapper, times(1)).map(order, OrderDTO.class);
    }

    @Test
    void testChangeOrderStatus_Success() {
        // Mock behavior
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        // Call the method
        OrderDTO result = orderService.changeOrderStatus(1, "SHIPPED");

        // Verify results
        assertNotNull(result);
        assertEquals("SHIPPED", result.getStatus());

        // Verify interactions
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(order, OrderDTO.class);
    }

    @Test
    void testChangeOrderStatus_OrderNotFound() {
        // Mock behavior
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // Call the method and verify exception
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.changeOrderStatus(1, "SHIPPED"));
        assertEquals("Order not found", exception.getMessage());

        // Verify interactions
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCancelOrder_Success() {
        // Mock behavior
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(new OrderResponse());

        // Call the method
        OrderResponse result = orderService.cancelOrder(1);

        // Verify results
        assertNotNull(result);
        assertEquals("Cancelled", order.getStatus());

        // Verify interactions
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(order, OrderResponse.class);
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        // Mock behavior
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // Call the method and verify exception
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(1));
        assertEquals("Order not found.", exception.getMessage());

        // Verify interactions
        verify(orderRepository, times(1)).findById(1);
        verify(orderRepository, never()).save(any());
    }
}
