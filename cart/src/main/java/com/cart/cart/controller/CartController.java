package com.cart.cart.controller;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.service.CartItemService;
import com.cart.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public List<CartDTO> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable int userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestBody int userId) {
        CartDTO createdCart = cartService.createCart(userId);
        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @PostMapping("/item")
    public ResponseEntity<CartItemDTO> addCartItem(@RequestBody CartItemDTO cartItemDTO) {
        CartItemDTO createdItem = cartItemService.addCartItem(cartItemDTO);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @GetMapping("/{cartId}/item")
    public List<CartItemDTO> getCartItemsByCartId(@PathVariable int cartId) {
        try {
            return cartItemService.getCartItemsByCartId(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving CartItems for cartId: " + cartId, e);
        }
    }

    @PutMapping("/item")
    public ResponseEntity<CartItemDTO> updateCartItem(@RequestBody CartItemDTO cartItemDTO) {
        CartItemDTO updatedItem = cartItemService.updateCartItem(cartItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable int itemId) {
        cartItemService.deleteCartItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCart(@PathVariable int id) {
        cartService.softDeleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteCartByUserId(@PathVariable int id) {
        cartService.deleteByUserId(id);
        return ResponseEntity.noContent().build();
    }
}
