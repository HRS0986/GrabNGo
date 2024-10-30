package com.auth.auth.model;

import com.auth.auth.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String Email;
    private String FirstName;
    private String LastName;
    private String Password;
    private String ContactNumber;
    private String NIC;
    private String Address;
    private UserRole Role;
}
