package com.cart.cart.service;

import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.exception.ResourceNotFoundException;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import com.cart.cart.repo.CartItemRepo;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {

    @Mock
    private CartItemRepo cartItemRepo;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartItemService cartItemService;

    private Cart cart;
    private CartItem cartItem;
    private CartItemDTO cartItemDTO;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1);
        cart.setTotalAmount(0);
        cart.setTotalPrice(0.0);

        cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setProductId(101);
        cartItem.setQuantity(2);
        cartItem.setPrice(50.0);
        cartItem.setCart(cart);

        cartItemDTO = new CartItemDTO(1, 101, 2, 50.0, 1);
    }

    @Test
    void testAddCartItem() {
        when(cartRepo.findById(cartItemDTO.getCartId())).thenReturn(Optional.of(cart));
        when(modelMapper.map(cartItemDTO, CartItem.class)).thenReturn(cartItem);
        when(cartItemRepo.save(cartItem)).thenReturn(cartItem);
        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDTO);

        CartItemDTO result = cartItemService.addCartItem(cartItemDTO);

        assertNotNull(result);
        assertEquals(cartItemDTO.getCartItemId(), result.getCartItemId());
        verify(cartRepo).save(cart);
    }

    @Test
    void testAddCartItem_CartNotFound() {
        when(cartRepo.findById(cartItemDTO.getCartId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartItemService.addCartItem(cartItemDTO));
        verify(cartRepo, never()).save(any(Cart.class));
    }

    @Test
    void testGetCartItemsByCartId() {
        when(cartItemRepo.findByCart_CartId(cart.getCartId())).thenReturn(List.of(cartItem));
        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDTO);

        List<CartItemDTO> result = cartItemService.getCartItemsByCartId(cart.getCartId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cartItemDTO.getCartItemId(), result.get(0).getCartItemId());
    }

    @Test
    void testUpdateCartItem() {
        when(cartItemRepo.findById(cartItemDTO.getCartItemId())).thenReturn(Optional.of(cartItem));
        when(cartRepo.findById(cartItemDTO.getCartId())).thenReturn(Optional.of(cart));
        when(cartItemRepo.save(cartItem)).thenReturn(cartItem);
        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDTO);

        CartItemDTO result = cartItemService.updateCartItem(cartItemDTO);

        assertNotNull(result);
        assertEquals(cartItemDTO.getQuantity(), result.getQuantity());
        verify(cartRepo).save(cart);
    }

    @Test
    void testDeleteCartItem() {
        when(cartItemRepo.findById(cartItemDTO.getCartItemId())).thenReturn(Optional.of(cartItem));
        when(cartRepo.findById(cartItem.getCart().getCartId())).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepo).deleteById(cartItemDTO.getCartItemId());

        cartItemService.deleteCartItem(cartItemDTO.getCartItemId());

        verify(cartItemRepo).deleteById(cartItemDTO.getCartItemId());
        verify(cartRepo).save(cart);
    }

    @Test
    void testDeleteCartItem_NotFound() {
        when(cartItemRepo.findById(cartItemDTO.getCartItemId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartItemService.deleteCartItem(cartItemDTO.getCartItemId()));
        verify(cartItemRepo, never()).deleteById(anyInt());
    }
}
