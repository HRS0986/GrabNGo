package com.order.order.config;

import com.order.order.dto.OrderDTO;
import com.order.order.dto.OrderItemDTO;
import com.order.order.model.Order;
import com.order.order.model.OrderItem;
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

        // mapping for OrderItem to OrderItemDTO
        modelMapper.createTypeMap(OrderItem.class, OrderItemDTO.class)
                .addMapping(src -> src.getOrder().getOrderId(), OrderItemDTO::setOrderId)
                .addMapping(OrderItem::getOrderItemId, OrderItemDTO::setOrderItemId)
                .addMapping(OrderItem::getQuantity, OrderItemDTO::setQuantity)
                .addMapping(OrderItem::getSellPrice, OrderItemDTO::setSellPrice);

        // mapping for Order to OrderDTO
        modelMapper.createTypeMap(Order.class, OrderDTO.class)
                .addMapping(Order::getOrderId, OrderDTO::setOrderId)
                .addMapping(Order::getUserId, OrderDTO::setUserId)
                .addMapping(Order::getTotalPrice, OrderDTO::setTotalPrice)
                .addMapping(Order::getDiscount, OrderDTO::setDiscount)
                .addMapping(Order::getStatus, OrderDTO::setStatus)
                .addMapping(Order::getCreatedDateTime, OrderDTO::setCreatedDateTime);

        return modelMapper;
    }

}
