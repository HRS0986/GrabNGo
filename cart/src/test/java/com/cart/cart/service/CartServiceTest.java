package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepo cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartById_Success() {
        // Arrange
        Cart cart = new Cart(1, 1, 100, 200.0, true, null);
        CartDTO expectedCartDTO = new CartDTO(1, 1, 100, 200.0, true);

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(expectedCartDTO);

        // Act
        CartDTO actualCartDTO = cartService.getCartById(1);

        // Assert
        assertNotNull(actualCartDTO);
        assertEquals(expectedCartDTO, actualCartDTO);
        verify(cartRepository, times(1)).findById(1);
    }

    @Test
    void testGetCartById_NotFound() {
        // Arrange
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.getCartById(1));
        verify(cartRepository, times(1)).findById(1);
    }

    @Test
    void testAddCart_Success() {
        // Arrange
        CartDTO cartDTO = new CartDTO(1, 1, 100, 200.0, true);
        Cart cart = new Cart(1, 1, 100, 200.0, true, null);

        when(modelMapper.map(cartDTO, Cart.class)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        // Act
        CartDTO savedCartDTO = cartService.createCart(cartDTO);

        // Assert
        assertNotNull(savedCartDTO);
        assertEquals(cartDTO, savedCartDTO);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testUpdateCart_Success() {
        // Arrange
        CartDTO cartDTO = new CartDTO(1, 1, 150, 300.0, true);
        Cart existingCart = new Cart(1, 1, 100, 200.0, true, null);
        Cart updatedCart = new Cart(1, 1, 150, 300.0, true, null);

        when(cartRepository.findById(1)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(existingCart)).thenReturn(updatedCart);
        when(modelMapper.map(updatedCart, CartDTO.class)).thenReturn(cartDTO);

        // Act
        CartDTO actualCartDTO = cartService.updateCart(1, cartDTO);

        // Assert
        assertNotNull(actualCartDTO);
        assertEquals(cartDTO, actualCartDTO);
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).save(existingCart);
    }

    @Test
    void testUpdateCart_NotFound() {
        // Arrange
        CartDTO cartDTO = new CartDTO(1, 1, 150, 300.0, true);

        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.updateCart(1, cartDTO));
        verify(cartRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteCart_Success() {
        // Arrange
        Cart cart = new Cart(1, 1, 100, 200.0, true, null);

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        doNothing().when(cartRepository).deleteById(1);

        // Act
        cartService.softDeleteCart(1);

        // Assert
        verify(cartRepository, times(1)).findById(1);
        verify(cartRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCart_NotFound() {
        // Arrange
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.softDeleteCart(1));
        verify(cartRepository, times(1)).findById(1);
    }
}
