package com.cart.cart.config;

import com.cart.cart.dto.CartDTO;
import com.cart.cart.dto.CartItemDTO;
import com.cart.cart.model.Cart;
import com.cart.cart.model.CartItem;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configure matching strategy to be stricter
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());

        // mapping for CartItem to CartItemDTO
        modelMapper.createTypeMap(CartItem.class, CartItemDTO.class)
                .addMapping(src -> src.getCart().getCartId(), CartItemDTO::setCartId)
                .addMapping(CartItem::getCartItemId, CartItemDTO::setCartItemId)
                .addMapping(CartItem::getProductId, CartItemDTO::setProductId)
                .addMapping(CartItem::getQuantity, CartItemDTO::setQuantity)
                .addMapping(CartItem::getPrice, CartItemDTO::setPrice);

        // mapping for Cart to CartDTO
        modelMapper.createTypeMap(Cart.class, CartDTO.class)
                .addMapping(Cart::getCartId, CartDTO::setCartId)
                .addMapping(Cart::getUserId, CartDTO::setUserId)
                .addMapping(Cart::getTotalAmount, CartDTO::setTotalAmount)
                .addMapping(Cart::getTotalPrice, CartDTO::setTotalPrice)
                .addMapping(Cart::isActive, CartDTO::setActive);

        return modelMapper;
    }

}
