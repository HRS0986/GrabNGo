package com.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO {
    private String Email;
    private String FirstName;
    private String LastName;
    private String Password;
    private String ContactNumber;
    private String NIC;
    private String Address;
}
