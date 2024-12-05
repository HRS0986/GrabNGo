package com.auth.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private JwtService mockJwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();

        // Use ReflectionTestUtils to inject mock values for @Value fields
        ReflectionTestUtils.setField(jwtService, "secretKey", "testsecretkeytestsecretkey=="); // Mock the secret key here (it should be base64 encoded)
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 1000 * 60 * 60); // 1 hour expiration
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 1000 * 60 * 60 * 24); // 1 day expiration
    }

    @Test
    void testGenerateAccessToken() {
        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtService.generateAccessToken(email);

        // Assert
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ")); // JWT token starts with 'eyJ'
    }

    @Test
    void testGenerateRefreshToken() {
        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtService.generateRefreshToken(email);

        // Assert
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ")); // JWT token starts with 'eyJ'
    }

    @Test
    void testIsTokenValid_WithValidToken() {
        // Arrange
        String email = "test@example.com";
        String validToken = jwtService.generateAccessToken(email);

        // Act
        boolean isValid = jwtService.isTokenValid(validToken, email);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_WithInvalidToken() {
        // Arrange
        String validEmail = "test@example.com";
        String invalidEmail = "invalid@example.com";
        String validToken = jwtService.generateAccessToken(validEmail);

        // Act
        boolean isValid = jwtService.isTokenValid(validToken, invalidEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testExtractEmailAddress() {
        // Arrange
        String email = "test@example.com";
        String token = jwtService.generateAccessToken(email);

        // Act
        String extractedEmail = jwtService.extractEmailAddress(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void testIsTokenExpired_WithExpiredToken() {
        // Arrange
        String email = "test@example.com";
        String expiredToken = generateExpiredToken(email);

        // Act
        boolean isExpired = jwtService.isTokenValid(expiredToken, email); // Using public method to validate token

        // Assert
        assertFalse(isExpired);  // Since the token is expired, it should be invalid.
    }

    private String generateExpiredToken(String email) {
        long expirationTime = -1000; // Set expiration to past time to simulate expired token

        // Use reflection to access the private method getSignedKey()
        Key key = (Key) ReflectionTestUtils.invokeMethod(jwtService, "getSignedKey");

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256) // Use the key here
                .compact();
    }
}
