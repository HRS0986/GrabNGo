package com.auth.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String emailAddress;
    private String password;
}
