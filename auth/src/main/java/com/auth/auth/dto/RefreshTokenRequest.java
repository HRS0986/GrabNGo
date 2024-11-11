package com.auth.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    @Valid

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    @NotBlank(message = "Access token is required")
    private String accessToken;
}
