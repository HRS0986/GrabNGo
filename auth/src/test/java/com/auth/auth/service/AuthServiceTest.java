package com.auth.auth.service;

import com.auth.auth.constants.Messages;
import com.auth.auth.dto.LoginRequest;
import com.auth.auth.dto.LoginResponse;
import com.auth.auth.dto.RefreshTokenRequest;
import com.auth.auth.dto.ResetPasswordRequest;
import com.auth.auth.dto.VerificationRequest;
import com.auth.auth.exception.UserNotFoundException;
import com.auth.auth.model.User;
import com.auth.auth.model.VerificationCode;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.repository.VerificationCodeRepository;
import com.auth.auth.utils.ActionResult;
import com.auth.auth.utils.VerificationCodeGenerator;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        // Prepare input
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");

        // Mock behavior
        User mockUser = new User();
        mockUser.setEmailAddress("user@example.com");
        when(authRepository.findByEmailAddress("user@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateAccessToken("user@example.com")).thenReturn("access_token");
        when(jwtService.generateRefreshToken("user@example.com")).thenReturn("refresh_token");

        // Call service method
        ActionResult result = authService.login(loginRequest);

        // Validate results
        assertTrue(result.getStatus());
        assertEquals(Messages.USER_AUTHENTICATED, result.getMessage());
        LoginResponse response = (LoginResponse) result.getData();
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    public void testRegister_Success() {
        // Prepare input
        User user = new User();
        user.setEmailAddress("user@example.com");
        user.setPassword("password123");

        // Mock behavior
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(authRepository.save(user)).thenReturn(user);

        // Call service method
        ActionResult result = authService.register(user);

        // Validate results
        assertTrue(result.getStatus());
        assertEquals(Messages.USER_CREATED_SUCCESS, result.getMessage());
    }

    @Test
    public void testTokenRefresh_Success() {
        // Prepare input
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("refresh_token", "user@example.com");

        // Mock behavior
        when(jwtService.extractEmailAddress("refresh_token")).thenReturn("user@example.com");
        User mockUser = new User();
        mockUser.setEmailAddress("user@example.com");
        when(authRepository.findByEmailAddress("user@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid("refresh_token", "user@example.com")).thenReturn(true);
        when(jwtService.generateAccessToken("user@example.com")).thenReturn("new_access_token");

        // Call service method
        ActionResult result = authService.tokenRefresh(refreshTokenRequest);

        // Validate results
        assertTrue(result.getStatus());
        assertEquals(Messages.TOKEN_REFRESHED, result.getMessage());
        assertEquals("new_access_token", result.getData());
    }

    @Test
    public void testForgetPassword_Success() throws MessagingException, IOException {
        // Arrange
        String email = "user@example.com";
        String verificationCode = "123456";
        String expectedUrl = "http://localhost/reset-password";  // This is the expected URL for the test

        // Create a mock User object
        User mockUser = new User();
        mockUser.setEmailAddress(email);
        mockUser.setPassword("oldPassword123");

        // Mock the AuthRepository to return the mock user when the email is searched
        when(authRepository.findByEmailAddress(email)).thenReturn(Optional.of(mockUser));

        // Mock the VerificationCodeGenerator to return a predictable code
        try (MockedStatic<VerificationCodeGenerator> mockedStatic = mockStatic(VerificationCodeGenerator.class)) {
            mockedStatic.when(VerificationCodeGenerator::generateCode).thenReturn(verificationCode);

            // Simulate generating a verification code and saving it in the repository
            VerificationCode mockVerificationCode = new VerificationCode();
            mockVerificationCode.setEmail(email);
            mockVerificationCode.setVerificationCode(verificationCode);
            mockVerificationCode.setValid(true); // Simulate a valid code

            // Mock the VerificationCodeRepository to save the mock verification code
            when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(mockVerificationCode);

            // Capture the arguments passed to sendForgetPasswordEmail
            ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

            // Mock the EmailService to simulate sending an email
            doNothing().when(emailService).sendForgetPasswordEmail(
                    emailCaptor.capture(),
                    codeCaptor.capture()
            );

            // Act
            ActionResult result = authService.forgetPassword(email);

            // Assert
            assertNotNull(result); // Ensure the result is not null
            assertTrue(result.getStatus()); // Ensure status is true (indicating success)
            assertEquals(Messages.EMAIL_SEND_SUCCESS, result.getMessage()); // Check the success message

            // Verify interactions with the mock repositories and services
            verify(authRepository, times(1)).findByEmailAddress(email);
            verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
            verify(emailService, times(1)).sendForgetPasswordEmail(
                    eq(email),
                    eq(verificationCode)
            );

            assertEquals(email, emailCaptor.getValue());
            assertEquals(verificationCode, codeCaptor.getValue());
            assertEquals(expectedUrl, urlCaptor.getValue());
        }
    }

    @Test
    public void testForgetPassword_UserNotFound() throws MessagingException, IOException {
        // Prepare input
        String email = "nonexistent@example.com";

        // Mock behavior
        when(authRepository.findByEmailAddress(email)).thenReturn(Optional.empty());

        // Call service method and assert exception
        assertThrows(UserNotFoundException.class, () -> authService.forgetPassword(email));
    }

    @Test
    public void testVerifyRequest_Success() {
        // Prepare input
        VerificationRequest verificationRequest = new VerificationRequest("user@example.com", "verification_code");

        // Mock behavior
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail("user@example.com");
        verificationCode.setVerificationCode("verification_code");
        verificationCode.setValid(true);
        when(verificationCodeRepository.findByCode("verification_code")).thenReturn(Optional.of(verificationCode));

        // Call service method
        ActionResult result = authService.verifyRequest(verificationRequest);

        // Validate results
        assertTrue(result.getStatus());
        assertEquals(Messages.VERIFICATION_SUCCESS, result.getMessage());
    }

    @Test
    public void testVerifyRequest_InvalidCode() {
        // Prepare input
        VerificationRequest verificationRequest = new VerificationRequest("user@example.com", "invalid_code");

        // Mock behavior
        when(verificationCodeRepository.findByCode("invalid_code")).thenReturn(Optional.empty());

        // Call service method and assert exception
        assertThrows(RuntimeException.class, () -> authService.verifyRequest(verificationRequest));
    }

    @Test
    void testResetPassword_Success() {
        // Arrange
        String email = "user@example.com";
        String verificationCode = "123456";
        String newPassword = "newPassword123";

        // Create a ResetPasswordRequest object (this might be your request DTO)
        ResetPasswordRequest request = new ResetPasswordRequest(email, newPassword, verificationCode);

        // Create a mock VerificationCode object to simulate a valid code
        VerificationCode mockVerificationCode = new VerificationCode();
        mockVerificationCode.setEmail(email);
        mockVerificationCode.setVerificationCode(verificationCode);
        mockVerificationCode.setValid(true); // Assuming the verification code is valid

        // Mock the VerificationCode repository to return the mock verification code
        when(verificationCodeRepository.findByCode(verificationCode))
                .thenReturn(Optional.of(mockVerificationCode));

        // Create a mock User object to simulate a user in the database
        User mockUser = new User();
        mockUser.setEmailAddress(email);  // Use setEmailAddress(), as Lombok generates this setter
        mockUser.setPassword("oldPassword123"); // Simulating the current password

        // Mock the AuthRepository to return the mock user
        when(authRepository.findByEmailAddress(email)).thenReturn(Optional.of(mockUser));

        // Mock the PasswordEncoder to simulate encoding the new password
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword123");

        // Act
        ActionResult result = authService.resetPassword(request);

        // Assert
        assertNotNull(result); // Ensure the result is not null
        assertTrue(result.getStatus()); // Check if status is true (indicating success)
        assertEquals(Messages.PASSWORD_RESET_SUCCESS, result.getMessage()); // Check the success message

        // Verify interactions with the mock repositories
        verify(verificationCodeRepository, times(1)).findByCode(verificationCode);
        verify(authRepository, times(1)).findByEmailAddress(email);
        verify(authRepository, times(1)).save(mockUser);  // Ensure the user is saved with the new password

        // Optionally, verify the password was updated in the user object
        assertEquals("encodedNewPassword123", mockUser.getPassword()); // Verify the password has been updated
    }

    @Test
    void testResetPassword_UserNotFound() {
        // Arrange
        String email = "nonexistentuser@example.com";
        String verificationCode = "123456";
        String newPassword = "newPassword123";

        ResetPasswordRequest request = new ResetPasswordRequest(email, newPassword, verificationCode);

        // Create a VerificationCode object with the required constructor parameters
        VerificationCode mockVerificationCode = new VerificationCode();
        mockVerificationCode.setEmail(email);
        mockVerificationCode.setVerificationCode(verificationCode);
        mockVerificationCode.setValid(true);

        when(verificationCodeRepository.findByCode(verificationCode))
                .thenReturn(Optional.of(mockVerificationCode));

        // Mock the authRepository to return an empty Optional (user not found)
        when(authRepository.findByEmailAddress(email)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.resetPassword(request);
        });

        assertEquals(Messages.USER_NOT_FOUND, exception.getMessage());

        // Verify interactions
        verify(verificationCodeRepository, times(1)).findByCode(verificationCode);
        verify(authRepository, times(1)).findByEmailAddress(email);
        verify(authRepository, never()).save(any());
    }
}
