package com.cart.cart.service;

import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import com.cart.cart.repo.CartItemRepo;
import com.cart.cart.repo.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartItemServiceTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private CartItemRepo cartItemRepo;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    

    @Test
    void testAddCartItem_Success() {
        int cartId = 1;
        Cart cart = new Cart();
        cart.setCartId(cartId);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(101);
        cartItemDTO.setQuantity(2);
        cartItemDTO.setPrice(200.0);

        CartItem cartItem = new CartItem();
        cartItem.setProductId(101);
        cartItem.setQuantity(2);
        cartItem.setPrice(200.0);
        cartItem.setCart(cart);

        CartItem savedCartItem = new CartItem();
        savedCartItem.setCartItemId(1);
        savedCartItem.setProductId(101);
        savedCartItem.setQuantity(2);
        savedCartItem.setPrice(200.0);
        savedCartItem.setCart(cart);

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(modelMapper.map(cartItemDTO, CartItem.class)).thenReturn(cartItem);
        when(cartItemRepo.save(cartItem)).thenReturn(savedCartItem);
        when(modelMapper.map(savedCartItem, CartItemDTO.class)).thenReturn(cartItemDTO);

        CartItemDTO result = cartItemService.addCartItem(cartId, cartItemDTO);

        assertNotNull(result);
        assertEquals(cartItemDTO.getProductId(), result.getProductId());
        verify(cartRepo, times(1)).findById(cartId);
        verify(cartItemRepo, times(1)).save(cartItem);
    }

    @Test
    void testGetCartItemsByCartId_Success() {
        int cartId = 1;
        CartItem cartItem1 = new CartItem();
        cartItem1.setCartItemId(1);
        cartItem1.setProductId(101);
        cartItem1.setQuantity(2);
        cartItem1.setPrice(200.0);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCartItemId(2);
        cartItem2.setProductId(102);
        cartItem2.setQuantity(1);
        cartItem2.setPrice(150.0);

        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        CartItemDTO cartItemDTO1 = new CartItemDTO();
        cartItemDTO1.setProductId(101);
        cartItemDTO1.setQuantity(2);
        cartItemDTO1.setPrice(200.0);

        CartItemDTO cartItemDTO2 = new CartItemDTO();
        cartItemDTO2.setProductId(102);
        cartItemDTO2.setQuantity(1);
        cartItemDTO2.setPrice(150.0);

        when(cartItemRepo.findByCart_CartId(cartId)).thenReturn(cartItems);
        when(modelMapper.map(cartItem1, CartItemDTO.class)).thenReturn(cartItemDTO1);
        when(modelMapper.map(cartItem2, CartItemDTO.class)).thenReturn(cartItemDTO2);

        List<CartItemDTO> result = cartItemService.getCartItemsByCartId(cartId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(cartItemDTO1.getProductId(), result.get(0).getProductId());
        verify(cartItemRepo, times(1)).findByCart_CartId(cartId);
    }

    @Test
    void testUpdateCartItem_Success() {
        int cartItemId = 1;

        CartItem existingCartItem = new CartItem();
        existingCartItem.setCartItemId(cartItemId);
        existingCartItem.setProductId(101);
        existingCartItem.setQuantity(2);
        existingCartItem.setPrice(200.0);

        CartItemDTO updatedCartItemDTO = new CartItemDTO();
        updatedCartItemDTO.setProductId(102);
        updatedCartItemDTO.setQuantity(3);
        updatedCartItemDTO.setPrice(250.0);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setCartItemId(cartItemId);
        updatedCartItem.setProductId(102);
        updatedCartItem.setQuantity(3);
        updatedCartItem.setPrice(250.0);

        when(cartItemRepo.findById(cartItemId)).thenReturn(Optional.of(existingCartItem));
        when(cartItemRepo.save(existingCartItem)).thenReturn(updatedCartItem);
        when(modelMapper.map(updatedCartItem, CartItemDTO.class)).thenReturn(updatedCartItemDTO);

        CartItemDTO result = cartItemService.updateCartItem(cartItemId, updatedCartItemDTO);

        assertNotNull(result);
        assertEquals(updatedCartItemDTO.getProductId(), result.getProductId());
        verify(cartItemRepo, times(1)).findById(cartItemId);
        verify(cartItemRepo, times(1)).save(existingCartItem);
    }

    @Test
    void testDeleteCartItem_Success() {
        int cartItemId = 1;

        when(cartItemRepo.existsById(cartItemId)).thenReturn(true);

        cartItemService.deleteCartItem(cartItemId);

        verify(cartItemRepo, times(1)).existsById(cartItemId);
        verify(cartItemRepo, times(1)).deleteById(cartItemId);
    }

    @Test
    void testDeleteCartItem_NotFound() {
        int cartItemId = 1;

        when(cartItemRepo.existsById(cartItemId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartItemService.deleteCartItem(cartItemId);
        });

        assertEquals("CartItem not found", exception.getMessage());
        verify(cartItemRepo, times(1)).existsById(cartItemId);
    }
}
