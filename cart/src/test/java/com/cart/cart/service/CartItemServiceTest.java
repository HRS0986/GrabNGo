package com.cart.cart.service;

import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import com.cart.cart.repo.CartItemRepo;
import com.cart.cart.repo.CartRepo;
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
class CartItemServiceTest {

    @InjectMocks
    private CartItemService cartItemService;

    @Mock
    private CartItemRepo cartItemRepo;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void addCartItem_ValidCartId_ReturnsSavedCartItemDTO() {
        int cartId = 1;
        CartItemDTO cartItemDTO = new CartItemDTO();
        Cart cart = new Cart();
        cart.setCartId(cartId);

        CartItem cartItem = new CartItem();
        CartItem savedCartItem = new CartItem();
        CartItemDTO savedCartItemDTO = new CartItemDTO();

        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(modelMapper.map(cartItemDTO, CartItem.class)).thenReturn(cartItem);
        when(cartItemRepo.save(cartItem)).thenReturn(savedCartItem);
        when(modelMapper.map(savedCartItem, CartItemDTO.class)).thenReturn(savedCartItemDTO);

        CartItemDTO result = cartItemService.addCartItem(cartId, cartItemDTO);

        assertNotNull(result);
        verify(cartRepo, times(1)).findById(cartId);
        verify(cartItemRepo, times(1)).save(cartItem);
    }

    @Test
    void addCartItem_InvalidCartId_ThrowsException() {
        int cartId = 999;
        CartItemDTO cartItemDTO = new CartItemDTO();

        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cartItemService.addCartItem(cartId, cartItemDTO));

        assertEquals("Cart not found", exception.getMessage());
        verify(cartRepo, times(1)).findById(cartId);
        verify(cartItemRepo, never()).save(any(CartItem.class));
    }

    @Test
    void getCartItemsByCartId_ValidCartId_ReturnsListOfCartItemDTO() {
        int cartId = 1;
        List<CartItem> cartItemList = List.of(new CartItem(), new CartItem());
        List<CartItemDTO> cartItemDTOList = List.of(new CartItemDTO(), new CartItemDTO());

        when(cartItemRepo.findByCart_CartId(cartId)).thenReturn(cartItemList);
        when(modelMapper.map(cartItemList.get(0), CartItemDTO.class)).thenReturn(cartItemDTOList.get(0));
        when(modelMapper.map(cartItemList.get(1), CartItemDTO.class)).thenReturn(cartItemDTOList.get(1));

        List<CartItemDTO> result = cartItemService.getCartItemsByCartId(cartId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cartItemRepo, times(1)).findByCart_CartId(cartId);
    }

    @Test
    void getCartItemsByCartId_InvalidCartId_ThrowsException() {
        int cartId = 999;

        when(cartItemRepo.findByCart_CartId(cartId)).thenThrow(new RuntimeException("Error retrieving CartItems for cartId: " + cartId));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cartItemService.getCartItemsByCartId(cartId));

        assertEquals("Error retrieving CartItems for cartId: " + cartId, exception.getMessage());
        verify(cartItemRepo, times(1)).findByCart_CartId(cartId);
    }

    @Test
    void updateCartItem_ValidCartItemId_ReturnsUpdatedCartItemDTO() {
        int cartItemId = 1;
        CartItemDTO cartItemDTO = new CartItemDTO();
        CartItem existingCartItem = new CartItem();
        CartItem updatedCartItem = new CartItem();
        CartItemDTO updatedCartItemDTO = new CartItemDTO();

        when(cartItemRepo.findById(cartItemId)).thenReturn(Optional.of(existingCartItem));
        when(cartItemRepo.save(existingCartItem)).thenReturn(updatedCartItem);
        when(modelMapper.map(updatedCartItem, CartItemDTO.class)).thenReturn(updatedCartItemDTO);

        CartItemDTO result = cartItemService.updateCartItem(cartItemId, cartItemDTO);

        assertNotNull(result);
        verify(cartItemRepo, times(1)).findById(cartItemId);
        verify(cartItemRepo, times(1)).save(existingCartItem);
    }

    @Test
    void updateCartItem_InvalidCartItemId_ThrowsException() {
        int cartItemId = 999;
        CartItemDTO cartItemDTO = new CartItemDTO();

        when(cartItemRepo.findById(cartItemId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cartItemService.updateCartItem(cartItemId, cartItemDTO));

        assertEquals("CartItem not found", exception.getMessage());
        verify(cartItemRepo, times(1)).findById(cartItemId);
        verify(cartItemRepo, never()).save(any(CartItem.class));
    }

    @Test
    void deleteCartItem_ValidCartItemId_DeletesCartItem() {
        int cartItemId = 1;

        when(cartItemRepo.existsById(cartItemId)).thenReturn(true);

        cartItemService.deleteCartItem(cartItemId);

        verify(cartItemRepo, times(1)).existsById(cartItemId);
        verify(cartItemRepo, times(1)).deleteById(cartItemId);
    }

    @Test
    void deleteCartItem_InvalidCartItemId_ThrowsException() {
        int cartItemId = 999;

        when(cartItemRepo.existsById(cartItemId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cartItemService.deleteCartItem(cartItemId));

        assertEquals("CartItem not found", exception.getMessage());
        verify(cartItemRepo, times(1)).existsById(cartItemId);
        verify(cartItemRepo, never()).deleteById(cartItemId);
    }
}
