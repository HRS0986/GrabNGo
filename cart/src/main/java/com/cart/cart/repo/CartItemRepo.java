package com.cart.cart.repo;

import com.cart.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepo extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart_CartId(int cartId);
}
