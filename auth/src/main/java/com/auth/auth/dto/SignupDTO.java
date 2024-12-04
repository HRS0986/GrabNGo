package com.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;
    private String contactNumber;
    private String address;
    private String nic;
}
