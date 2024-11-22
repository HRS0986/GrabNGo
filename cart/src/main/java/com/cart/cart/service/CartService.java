package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.repo.CartRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    private final CartRepo cartRepo;
    private final ModelMapper modelMapper;

    public CartService(CartRepo cartRepo, ModelMapper modelMapper) {
        this.cartRepo = cartRepo;
        this.modelMapper = modelMapper;
    }

    public List<CartDTO> getAllCarts(){
        List<Cart>cartList = cartRepo.findAll();
        return modelMapper.map(cartList, new TypeToken<List<CartDTO>>(){}.getType());
    }

    public CartDTO getCartById(int cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return modelMapper.map(cart, CartDTO.class);
    }

    public CartDTO createCart(CartDTO cartDTO) {
        Cart cart = modelMapper.map(cartDTO, Cart.class);
        Cart savedCart = cartRepo.save(cart);
        return modelMapper.map(savedCart, CartDTO.class);
    }

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

    public void deleteByUserId(int id) {
        Optional<Cart> cartOptional = cartRepo.findByUserId(id);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cart.setActive(false);
            cartRepo.save(cart);
        }
    }
}
