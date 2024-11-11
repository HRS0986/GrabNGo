package com.order.order.repository;

import com.order.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
//    List<Order> findByUserId(int userId);
//
//    List<Order> findByStatus(String status);
//
//    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems")
//    List<Order> findAllWithOrderItems();

    @Query("SELECT o FROM Order o WHERE "
            + "(:userId IS NULL OR o.userId = :userId) AND "
            + "(:status IS NULL OR o.status = :status)")
    List<Order> findOrdersByCriteria(@Param("userId") Integer userId, @Param("status") String status);
}
