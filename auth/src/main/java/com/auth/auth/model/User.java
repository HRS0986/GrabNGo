package com.auth.auth.model;

import com.auth.auth.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private int userId;

    @Column(unique = true)
    @NotBlank(message = "Email Address is required")
    @Email
    private String emailAddress;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    @NotBlank
    private String contactNumber;

    @Column(unique = true)
    @NotBlank
    private String nic;

    private String address;

    @NotNull(message = "Role is required")
    private UserRole role;
}
