package com.order.order.service;

import com.order.order.dto.OrderDTO;
import com.order.order.model.Order;
import com.order.order.model.OrderItem;
import com.order.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setup() {

        order = new Order();
        order.setOrderId(1);
        order.setStatus("Accepted");

        orderDTO = new OrderDTO();
        orderDTO.setOrderId(1);
        orderDTO.setStatus("Accepted");
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDTO.getOrderId(), result.get(0).getOrderId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testPlaceOrder() {
        when(modelMapper.map(orderDTO, Order.class)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.placeOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getOrderId(), result.getOrderId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testChangeOrderStatus() {
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.changeOrderStatus(1, "Shipped");

        assertNotNull(result);
        assertEquals("Shipped", order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCancelOrder() {
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.cancelOrder(1);

        assertNotNull(result);
        assertEquals("Cancelled", order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderDTO.class)).thenReturn(orderDTO);

        OrderDTO result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(orderDTO.getOrderId(), result.getOrderId());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        OrderDTO result = orderService.getOrderById(1);

        assertNull(result);
        verify(orderRepository, times(1)).findById(1);
    }
}
