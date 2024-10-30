package com.auth.auth.model;

import com.auth.auth.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int UserId;

    @Column(unique = true, nullable = false)
    private String Email;

    @Column(nullable = false)
    private String FirstName;

    @Column(nullable = false)
    private String LastName;

    @Column(nullable = false)
    private String Password;

    @Column(nullable = false)
    private String ContactNumber;

    @Column(unique = true, nullable = false)
    private String NIC;

    @Column(nullable = false)
    private String Address;

    @Column(nullable = false)

    private UserRole Role;
}
