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
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartDTO> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable int id) {
        CartDTO cart = cartService.getCartById(id);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestBody CartDTO cartDTO) {
        CartDTO createdCart = cartService.createCart(cartDTO);
        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable int id, @RequestBody CartDTO cartDTO) {
        CartDTO updatedCart = cartService.updateCart(id, cartDTO);
        return ResponseEntity.ok(updatedCart);
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
