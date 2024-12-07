package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.exception.ResourceNotFoundException;
import com.cart.cart.model.Cart;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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

    private Cart cart;
    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1);
        cart.setUserId(101);
        cart.setTotalAmount(5);
        cart.setTotalPrice(150.0);
        cart.setActive(true);

        cartDTO = new CartDTO();
        cartDTO.setCartId(1);
        cartDTO.setUserId(101);
        cartDTO.setTotalAmount(5);
        cartDTO.setTotalPrice(150.0);
        cartDTO.setActive(true);
    }

    @Test
    void testGetAllCarts_Success() {
        List<Cart> carts = Arrays.asList(cart);
        List<CartDTO> cartDTOs = Arrays.asList(cartDTO);

        when(cartRepo.findAll()).thenReturn(carts);
        when(modelMapper.map(carts, new org.modelmapper.TypeToken<List<CartDTO>>() {}.getType())).thenReturn(cartDTOs);

        List<CartDTO> result = cartService.getAllCarts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cartRepo, times(1)).findAll();
        verify(modelMapper, times(1)).map(carts, new org.modelmapper.TypeToken<List<CartDTO>>() {}.getType());
    }

    @Test
    void testGetCartByUserId_Success() {
        when(cartRepo.findByUserId(cart.getUserId())).thenReturn(Optional.of(cart));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartByUserId(cart.getUserId());

        assertNotNull(result);
        assertEquals(cartDTO.getUserId(), result.getUserId());
        verify(cartRepo, times(1)).findByUserId(cart.getUserId());
        verify(modelMapper, times(1)).map(cart, CartDTO.class);
    }

    @Test
    void testGetCartByUserId_NotFound() {
        when(cartRepo.findByUserId(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> cartService.getCartByUserId(999));

        assertEquals("Cart not found with the User ID 999", exception.getMessage());
        verify(cartRepo, times(1)).findByUserId(999);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testCreateCart_NewCart() {
        when(cartRepo.findByUserId(cart.getUserId())).thenReturn(Optional.empty());
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(cart.getUserId());

        assertNotNull(result);
        assertEquals(cartDTO.getUserId(), result.getUserId());
        verify(cartRepo, times(1)).findByUserId(cart.getUserId());
        verify(cartRepo, times(1)).save(any(Cart.class));
        verify(modelMapper, times(1)).map(cart, CartDTO.class);
    }

    @Test
    void testCreateCart_ExistingCart() {
        when(cartRepo.findByUserId(cart.getUserId())).thenReturn(Optional.of(cart));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);

        CartDTO result = cartService.createCart(cart.getUserId());

        assertNotNull(result);
        assertEquals(cartDTO.getUserId(), result.getUserId());
        verify(cartRepo, times(1)).findByUserId(cart.getUserId());
        verifyNoMoreInteractions(cartRepo);
        verify(modelMapper, times(1)).map(cart, CartDTO.class);
    }

    @Test
    void testSoftDeleteCart_Success() {
        when(cartRepo.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        cartService.softDeleteCart(cart.getCartId());

        assertFalse(cart.isActive());
        verify(cartRepo, times(1)).findById(cart.getCartId());
        verify(cartRepo, times(1)).save(cart);
    }

    @Test
    void testDeleteByUserId_Success() {
        when(cartRepo.findByUserId(cart.getUserId())).thenReturn(Optional.of(cart));

        cartService.deleteByUserId(cart.getUserId());

        assertFalse(cart.isActive());
        verify(cartRepo, times(1)).findByUserId(cart.getUserId());
        verify(cartRepo, times(1)).save(cart);
    }
}
