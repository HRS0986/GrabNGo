package com.auth.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;
    private String contactNumber;
    private String nic;
    private String address;
}
