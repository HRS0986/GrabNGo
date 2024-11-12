package com.cart.cart.controller;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/cart/{cartId}/cartItems")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;
//
//    // Get all Cart Items
//    @GetMapping
//    public List<CartItemDTO> getAllCartItems() {
//        return cartItemService.getAllCartItems();
//    }

    // Add an item to the cart
    @PostMapping
    public ResponseEntity<CartItemDTO> addCartItem(@PathVariable int cartId, @RequestBody CartItemDTO cartItemDTO) {
        CartItemDTO createdItem = cartItemService.addCartItem(cartId, cartItemDTO);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    // Get all Cart Items by Cart ID with error handling
    @GetMapping
    public List<CartItemDTO> getCartItemsByCartId(@PathVariable int cartId) {
        try {
            return cartItemService.getCartItemsByCartId(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving CartItems for cartId: " + cartId, e);
        }
    }

    // Update a specific cart item
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable int cartItemId, @RequestBody CartItemDTO cartItemDTO) {
        CartItemDTO updatedItem = cartItemService.updateCartItem(cartItemId, cartItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    // Delete a specific cart item
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable int cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
