package com.auth.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {
    private String EmailAddress;
    private String FirstName;
    private String LastName;
    private String Password;
    private String ContactNumber;
    private String NIC;
    private String Address;
}
