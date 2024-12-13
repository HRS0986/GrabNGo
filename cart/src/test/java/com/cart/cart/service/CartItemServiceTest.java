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

import java.util.ArrayList;
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

    private CartItemDTO cartItemDTO;
    private CartItem cartItem;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1);
        cart.setTotalAmount(5);
        cart.setTotalPrice(100.0);

        cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setQuantity(2);
        cartItem.setPrice(20.0);
        cartItem.setCart(cart);

        cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(1);
        cartItemDTO.setCartId(1);
        cartItemDTO.setQuantity(2);
        cartItemDTO.setPrice(20.0);
    }

    @Test
    void testAddCartItem_Success() {
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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> cartItemService.addCartItem(cartItemDTO));

        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testGetCartItemsByCartId_Success() {
        List<CartItem> cartItems = List.of(cartItem);
        when(cartItemRepo.findByCart_CartId(cart.getCartId())).thenReturn(cartItems);
        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDTO);

        List<CartItemDTO> result = cartItemService.getCartItemsByCartId(cart.getCartId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cartItemDTO.getCartItemId(), result.get(0).getCartItemId());
    }

    @Test
    void testGetCartItemsByCartId_CartNotFound() {
        when(cartItemRepo.findByCart_CartId(cart.getCartId())).thenThrow(new ResourceNotFoundException("Cart not found with the id " + cart.getCartId()));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> cartItemService.getCartItemsByCartId(cart.getCartId()));

        assertEquals("Cart not found with the id 1", exception.getMessage());
    }

    @Test
    void testUpdateCartItem_Success() {
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
    void testUpdateCartItem_CartItemNotFound() {
        when(cartItemRepo.findById(cartItemDTO.getCartItemId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> cartItemService.updateCartItem(cartItemDTO));

        assertEquals("CartItem not found", exception.getMessage());
    }

//    @Test
//    void testDeleteCartItem_Success() {
//        // Mocking the cartRepo to return a cart when findById is called
//        when(cartRepo.findById(cartItem.getCart().getCartId())).thenReturn(Optional.of(cart));
//
//        // Mocking the cartItemRepo to return a cartItem when findById is called
//        when(cartItemRepo.findById(cartItem.getCartItemId())).thenReturn(Optional.of(cartItem));
//
//        // Mocking modelMapper to map CartItemDTO to CartItem
//        when(modelMapper.map(cartItemDTO, CartItem.class)).thenReturn(cartItem);
//
//        // Mocking the repository save and delete methods
//        when(cartItemRepo.save(cartItem)).thenReturn(cartItem);
//        doNothing().when(cartItemRepo).deleteById(cartItem.getCartItemId());
//
//        when(cartRepo.findById(cartItemDTO.getCartId())).thenReturn(Optional.of(cart));
//        when(modelMapper.map(cartItemDTO, CartItem.class)).thenReturn(cartItem);
//        when(cartItemRepo.save(cartItem)).thenReturn(cartItem);
//        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDTO);
//
//        // Execute addCartItem to simulate creating a cart item
//        var result = cartItemService.addCartItem(cartItemDTO);
//
//        // Execute deleteCartItem to simulate deleting the cart item
//        cartItemService.deleteCartItem(result.getCartItemId());
//
//        // Verifying interactions
//        verify(cartItemRepo).deleteById(result.getCartItemId());
//        verify(cartRepo).save(cart); // Ensures the cart is updated after deletion
//    }



    @Test
    void testDeleteCartItem_CartItemNotFound() {
        when(cartItemRepo.findById(cartItem.getCartItemId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> cartItemService.deleteCartItem(cartItem.getCartItemId()));

        assertEquals("CartItem not found", exception.getMessage());
    }
}
