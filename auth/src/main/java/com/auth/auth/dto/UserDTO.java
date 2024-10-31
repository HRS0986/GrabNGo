package com.auth.auth.dto;

import com.auth.auth.enums.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int userId;
    private String email;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String nic;
    private String address;
    private UserRole role;
}
