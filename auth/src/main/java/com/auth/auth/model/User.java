package com.auth.auth.model;

import com.auth.auth.constants.Messages;
import com.auth.auth.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = Messages.EMAIL_REQUIRED)
    @Email(message = Messages.INVALID_EMAIL)
    private String emailAddress;

    @NotBlank(message = Messages.FIRSTNAME_REQUIRED)
    private String firstName;

    @NotBlank(message = Messages.LASTNAME_REQUIRED)
    private String lastName;

    @NotBlank(message = Messages.PASSWORD_REQUIRED)
    private String password;

    @NotBlank(message = Messages.CONTACT_REQUIRED)
    @Pattern(regexp = "^(?:\\+94|0)?(?:7\\d{8})$", message = Messages.INVALID_CONTACT)
    private String contactNumber;

    @Pattern(regexp = "(^[0-9]{9}[vVxX]$|^[0-9]{12}$)", message = Messages.INVALID_NIC)
    @NotBlank(message = Messages.NIC_REQUIRED)
    private String nic;

    @NotBlank(message = Messages.ADDRESS_REQUIRED)
    private String address;

    private UserRole role;

    private boolean isActive = true;
}
