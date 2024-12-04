package com.cart.cart.service;

import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.exception.ResourceNotFoundException;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import com.cart.cart.repo.CartItemRepo;
import com.cart.cart.repo.CartRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    private final CartItemRepo cartItemRepo;
    private final CartRepo cartRepo;
    private final ModelMapper modelMapper;

    public CartItemService(CartItemRepo cartItemRepo, CartRepo cartRepo, ModelMapper modelMapper) {
        this.cartItemRepo = cartItemRepo;
        this.cartRepo = cartRepo;
        this.modelMapper = modelMapper;
    }

    public CartItemDTO addCartItem(CartItemDTO cartItemDTO) {
        Cart cart = cartRepo.findById(cartItemDTO.getCartId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        CartItem cartItem = modelMapper.map(cartItemDTO, CartItem.class);
        cartItem.setCart(cart);
        CartItem savedCartItem = cartItemRepo.save(cartItem);
        cart.setTotalAmount(cart.getTotalAmount() + cartItemDTO.getQuantity());
        var totalPrice = cartItemDTO.getPrice() * cartItemDTO.getQuantity();
        cart.setTotalPrice(cart.getTotalPrice() + totalPrice);
        cartRepo.save(cart);
        return modelMapper.map(savedCartItem, CartItemDTO.class);
    }

    public List<CartItemDTO> getCartItemsByCartId(int cartId) {
        List<CartItem> cartItemList;
        try {
            cartItemList = cartItemRepo.findByCart_CartId(cartId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Cart not found with the id " + cartId);
        }

        return cartItemList.stream()
                .map(item -> {
                    try {
                        return modelMapper.map(item, CartItemDTO.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error mapping CartItem to CartItemDTO", e);
                    }
                })
                .collect(Collectors.toList());
    }

    // Update a CartItem (Modify Item Quantity)
    public CartItemDTO updateCartItem(CartItemDTO cartItemDTO) {
        return handleCartItemQuantityChange(cartItemDTO);
    }

    private CartItemDTO handleCartItemQuantityChange(CartItemDTO cartItemDTO) {
        CartItem existingCartItem = cartItemRepo.findById(cartItemDTO.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found"));

        Cart cart = cartRepo.findById(cartItemDTO.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        int currentAmount = existingCartItem.getQuantity();
        int diff = cartItemDTO.getQuantity() - currentAmount;
        int newTotalCartItemsAmount = cart.getTotalAmount() + diff;

        existingCartItem.setQuantity(cartItemDTO.getQuantity());
        CartItem updatedCartItem = cartItemRepo.save(existingCartItem);

        cart.setTotalAmount(newTotalCartItemsAmount);
        double newTotalPrice = cart.getTotalPrice() + (existingCartItem.getPrice() * diff);
        cart.setTotalPrice(newTotalPrice);
        cartRepo.save(cart);

        if (cartItemDTO.getQuantity() == 0) {
            if (!cartItemRepo.existsById(cartItemDTO.getCartItemId())) {
                throw new ResourceNotFoundException("CartItem not found");
            }
            cartItemRepo.deleteById(cartItemDTO.getCartItemId());
        }
        return modelMapper.map(updatedCartItem, CartItemDTO.class);
    }

    // Delete a CartItem
    public void deleteCartItem(int cartItemId) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found"));
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(cartItemId);
        cartItemDTO.setCartId(cartItem.getCart().getCartId());
        cartItemDTO.setQuantity(0);
        cartItemDTO.setPrice(cartItem.getPrice());
        handleCartItemQuantityChange(cartItemDTO);
    }
}
