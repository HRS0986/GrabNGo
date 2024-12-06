package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCarts() {

        List<Cart> cartList = mock(List.class);
        List<CartDTO> cartDTOList = mock(List.class);

        when(cartRepo.findAll()).thenReturn(cartList);
        when(modelMapper.map(cartList, new org.modelmapper.TypeToken<List<CartDTO>>() {}.getType())).thenReturn(cartDTOList);


        List<CartDTO> result = cartService.getAllCarts();


        assertNotNull(result);
        verify(cartRepo, times(1)).findAll();
        verify(modelMapper, times(1)).map(cartList, new org.modelmapper.TypeToken<List<CartDTO>>() {}.getType());
    }

    @Test
    void testGetCartById_Success() {

        Cart mockCart = mock(Cart.class);
        CartDTO mockCartDTO = mock(CartDTO.class);

        when(cartRepo.findById(anyInt())).thenReturn(Optional.of(mockCart));
        when(modelMapper.map(mockCart, CartDTO.class)).thenReturn(mockCartDTO);


        CartDTO result = cartService.getCartById(1);


        assertNotNull(result);
        verify(cartRepo, times(1)).findById(anyInt());
        verify(modelMapper, times(1)).map(mockCart, CartDTO.class);
    }

    @Test
    void testGetCartById_NotFound() {

        when(cartRepo.findById(anyInt())).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> cartService.getCartById(1));
        assertEquals("Cart not found", exception.getMessage());
        verify(cartRepo, times(1)).findById(anyInt());
    }

    @Test
    void testCreateCart() {

        Cart mockCart = mock(Cart.class);
        Cart savedCart = mock(Cart.class);
        CartDTO mockCartDTO = mock(CartDTO.class);
        CartDTO savedCartDTO = mock(CartDTO.class);

        when(modelMapper.map(mockCartDTO, Cart.class)).thenReturn(mockCart);
        when(cartRepo.save(mockCart)).thenReturn(savedCart);
        when(modelMapper.map(savedCart, CartDTO.class)).thenReturn(savedCartDTO);


        CartDTO result = cartService.createCart(mockCartDTO);


        assertNotNull(result);
        verify(cartRepo, times(1)).save(mockCart);
        verify(modelMapper, times(1)).map(mockCartDTO, Cart.class);
        verify(modelMapper, times(1)).map(savedCart, CartDTO.class);
    }

    @Test
    void testUpdateCart() {

        Cart existingCart = mock(Cart.class);
        Cart updatedCart = mock(Cart.class);
        CartDTO updatedCartDTO = mock(CartDTO.class);

        when(cartRepo.findById(anyInt())).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(existingCart)).thenReturn(updatedCart);
        when(modelMapper.map(updatedCart, CartDTO.class)).thenReturn(updatedCartDTO);


        CartDTO result = cartService.updateCart(1, updatedCartDTO);


        assertNotNull(result);
        verify(cartRepo, times(1)).findById(anyInt());
        verify(cartRepo, times(1)).save(existingCart);
        verify(modelMapper, times(1)).map(updatedCart, CartDTO.class);
    }

    @Test
    void testSoftDeleteCart() {

        Cart mockCart = mock(Cart.class);

        when(cartRepo.findById(anyInt())).thenReturn(Optional.of(mockCart));


        cartService.softDeleteCart(1);


        verify(cartRepo, times(1)).findById(anyInt());
        verify(cartRepo, times(1)).save(mockCart);
    }
}
