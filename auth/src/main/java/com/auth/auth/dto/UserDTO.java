package com.auth.auth.dto;

import com.auth.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int UserId;
    private String Email;
    private String FirstName;
    private String LastName;
    private String ContactNumber;
    private String NIC;
    private String Address;
    private UserRole Role;
}
