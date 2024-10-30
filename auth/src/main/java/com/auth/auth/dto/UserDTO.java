package com.auth.auth.dto;

import com.auth.auth.enums.UserRole;
import lombok.*;

@Data
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
