package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.repo.CartRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ModelMapper modelMapper;

    // Get All Carts
    public List<CartDTO> getAllCarts(){
        List<Cart>cartList = cartRepo.findAll();
        return modelMapper.map(cartList, new TypeToken<List<CartDTO>>(){}.getType());
    }

    // Get Cart by ID
    public CartDTO getCartById(int cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return modelMapper.map(cart, CartDTO.class);
    }

    // Create a new Cart
    public CartDTO createCart(CartDTO cartDTO) {
        Cart cart = modelMapper.map(cartDTO, Cart.class);
        Cart savedCart = cartRepo.save(cart);
        return modelMapper.map(savedCart, CartDTO.class);
    }

    // Update an existing Cart
    public CartDTO updateCart(int cartId, CartDTO cartDTO) {
        Cart existingCart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Update fields
        existingCart.setUserId(cartDTO.getUserId());
        existingCart.setTotalAmount(cartDTO.getTotalAmount());
        existingCart.setTotalPrice(cartDTO.getTotalPrice());
        existingCart.setActive(cartDTO.isActive());

        Cart updatedCart = cartRepo.save(existingCart);
        return modelMapper.map(updatedCart, CartDTO.class);
    }

    // Soft delete a Cart (set isActive to false)
    public void softDeleteCart(int cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setActive(false);
        cartRepo.save(cart);
    }
}
