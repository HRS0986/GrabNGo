package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartService cartService;

    // Test for getAllCarts
    @Test
    void testGetAllCarts_Success() {
        // Arrange
        List<Cart> mockCarts = List.of(
                new Cart(1, 101, 2, 100.0, true,null),
                new Cart(2, 102, 3, 150.0, true,null)
        );
        List<CartDTO> mockCartDTOs = List.of(
                new CartDTO(1, 101, 2, 100.0, true),
                new CartDTO(2, 102, 3, 150.0, true)
        );

        when(cartRepo.findAll()).thenReturn(mockCarts);
        when(modelMapper.map(mockCarts, new org.modelmapper.TypeToken<List<CartDTO>>() {}.getType())).thenReturn(mockCartDTOs);

        // Act
        List<CartDTO> cartDTOs = cartService.getAllCarts();

        // Assert
        assertEquals(2, cartDTOs.size());
        assertEquals(mockCartDTOs, cartDTOs);

        verify(cartRepo, times(1)).findAll();
        verify(modelMapper, times(1)).map(mockCarts, new org.modelmapper.TypeToken<List<CartDTO>>() {}.getType());
    }

    // Test for getCartById
    @Test
    void testGetCartById_Success() {
        // Arrange
        Cart mockCart = new Cart(1, 101, 2, 100.0, true,null);
        CartDTO mockCartDTO = new CartDTO(1, 101, 2, 100.0, true);

        when(cartRepo.findById(1)).thenReturn(Optional.of(mockCart));
        when(modelMapper.map(mockCart, CartDTO.class)).thenReturn(mockCartDTO);

        // Act
        CartDTO cartDTO = cartService.getCartById(1);

        // Assert
        assertNotNull(cartDTO);
        assertEquals(mockCartDTO, cartDTO);

        verify(cartRepo, times(1)).findById(1);
        verify(modelMapper, times(1)).map(mockCart, CartDTO.class);
    }

    @Test
    void testGetCartById_NotFound() {
        // Arrange
        when(cartRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cartService.getCartById(1));
        assertEquals("Cart not found", exception.getMessage());

        verify(cartRepo, times(1)).findById(1);
    }

    // Test for createCart
    @Test
    void testCreateCart_Success() {
        // Arrange
        CartDTO cartDTO = new CartDTO(1, 101, 2, 100.0, true);
        Cart cart = new Cart(1, 101, 2, 100.0, true,null);
        Cart savedCart = new Cart(1, 101, 2, 100.0, true,null);

        when(modelMapper.map(cartDTO, Cart.class)).thenReturn(cart);
        when(cartRepo.save(cart)).thenReturn(savedCart);
        when(modelMapper.map(savedCart, CartDTO.class)).thenReturn(cartDTO);

        // Act
        CartDTO result = cartService.createCart(cartDTO);

        // Assert
        assertNotNull(result);
        assertEquals(cartDTO, result);

        verify(cartRepo, times(1)).save(cart);
        verify(modelMapper, times(1)).map(cartDTO, Cart.class);
        verify(modelMapper, times(1)).map(savedCart, CartDTO.class);
    }

    // Test for updateCart
    @Test
    void testUpdateCart_Success() {
        // Arrange
        CartDTO cartDTO = new CartDTO(1, 101, 2, 200.0, true);
        Cart existingCart = new Cart(1, 101, 2, 100.0, true,null);
        Cart updatedCart = new Cart(1, 101, 2, 200.0, true,null);

        when(cartRepo.findById(1)).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(existingCart)).thenReturn(updatedCart);
        when(modelMapper.map(updatedCart, CartDTO.class)).thenReturn(cartDTO);

        // Act
        CartDTO result = cartService.updateCart(1, cartDTO);

        // Assert
        assertNotNull(result);
        assertEquals(cartDTO, result);

        verify(cartRepo, times(1)).findById(1);
        verify(cartRepo, times(1)).save(existingCart);
        verify(modelMapper, times(1)).map(updatedCart, CartDTO.class);
    }

    @Test
    void testUpdateCart_NotFound() {
        // Arrange
        CartDTO cartDTO = new CartDTO(1, 101, 2, 200.0, true);
        when(cartRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cartService.updateCart(1, cartDTO));
        assertEquals("Cart not found", exception.getMessage());

        verify(cartRepo, times(1)).findById(1);
    }

    // Test for softDeleteCart
    @Test
    void testSoftDeleteCart_Success() {
        // Arrange
        Cart existingCart = new Cart(1, 101, 2, 100.0, true,null);
        Cart updatedCart = new Cart(1, 101, 2, 100.0, false,null);

        when(cartRepo.findById(1)).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(existingCart)).thenReturn(updatedCart);

        // Act
        cartService.softDeleteCart(1);

        // Assert
        assertFalse(existingCart.isActive());
        verify(cartRepo, times(1)).findById(1);
        verify(cartRepo, times(1)).save(existingCart);
    }

    @Test
    void testSoftDeleteCart_NotFound() {
        // Arrange
        when(cartRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cartService.softDeleteCart(1));
        assertEquals("Cart not found", exception.getMessage());

        verify(cartRepo, times(1)).findById(1);
    }
}
