package com.order.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdDateTime = LocalDateTime.now();

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = true, length = 50)
    private String firstname;

    @Column(nullable = true, length = 50)
    private String lastname;

    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 100)
    private String apartment;

    @Column(nullable = true, length = 50)
    private String city;

    @Column(nullable = true, length = 50)
    private String country;

    @Column(nullable = true, length = 20)
    private String zipcode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItem> orderItems;

}
