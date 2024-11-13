package com.cart.cart.service;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import com.cart.cart.repo.CartItemRepo;
import com.cart.cart.repo.CartRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ModelMapper modelMapper;

//    // Get All Cart Items
//    public List<CartItemDTO> getAllCartItems(){
//        List<CartItem>cartItemList = cartItemRepo.findAll();
//        return modelMapper.map(cartItemList, new TypeToken<List<CartItemDTO>>(){}.getType());
//    }

    // Add CartItem to a Cart
    public CartItemDTO addCartItem(int cartId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem cartItem = modelMapper.map(cartItemDTO, CartItem.class);
        cartItem.setCart(cart);

        CartItem savedCartItem = cartItemRepo.save(cartItem);
        return modelMapper.map(savedCartItem, CartItemDTO.class);
    }

    // Get Cart Items by Cart ID with error handling for debugging
    public List<CartItemDTO> getCartItemsByCartId(int cartId) {
        List<CartItem> cartItemList;
        try {
            cartItemList = cartItemRepo.findByCart_CartId(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving CartItems for cartId: " + cartId, e);
        }

        // Check if cart items exist and then map
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

    // Update a CartItem
    public CartItemDTO updateCartItem(int cartItemId, CartItemDTO cartItemDTO) {
        CartItem existingCartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        existingCartItem.setProductId(cartItemDTO.getProductId());
        existingCartItem.setQuantity(cartItemDTO.getQuantity());
        existingCartItem.setPrice(cartItemDTO.getPrice());

        CartItem updatedCartItem = cartItemRepo.save(existingCartItem);
        return modelMapper.map(updatedCartItem, CartItemDTO.class);
    }

    // Delete a CartItem
    public void deleteCartItem(int cartItemId) {
        if (!cartItemRepo.existsById(cartItemId)) {
            throw new RuntimeException("CartItem not found");
        }
        cartItemRepo.deleteById(cartItemId);
    }
}
