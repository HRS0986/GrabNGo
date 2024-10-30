package com.auth.auth.dto;

import lombok.*;

@Data
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
