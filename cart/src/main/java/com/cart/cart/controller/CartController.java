package com.cart.cart.controller;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Get all carts
    @GetMapping
    public List<CartDTO> getAllCarts() {
        return cartService.getAllCarts();
    }

    // Get cart by ID
    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable int id) {
        CartDTO cart = cartService.getCartById(id);
        return ResponseEntity.ok(cart);
    }

    // Create a new cart
    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO) {
        CartDTO createdCart = cartService.createCart(cartDTO);
        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    // Update an existing cart
    @PutMapping("/{id}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable int id, @RequestBody CartDTO cartDTO) {
        CartDTO updatedCart = cartService.updateCart(id, cartDTO);
        return ResponseEntity.ok(updatedCart);
    }

    // Soft delete a cart (set isActive to false)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCart(@PathVariable int id) {
        cartService.softDeleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
