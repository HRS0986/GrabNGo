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

    private String userEmail = "user@example.com";
    private String password = "password123";
    private String newPassword = "newPassword123";
    private String verificationCode = "123456";
    private String refreshToken = "refresh_token";
    private String accessToken = "access_token";
    private String invalidEmail = "nonexistent@example.com";
    private String invalidVerificationCode = "invalid_code";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest(userEmail, password);

        User mockUser = new User();
        mockUser.setEmailAddress(userEmail);
        when(authRepository.findByEmailAddress(userEmail)).thenReturn(Optional.of(mockUser));
        when(jwtService.generateAccessToken(userEmail)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(userEmail)).thenReturn(refreshToken);

        ActionResult result = authService.login(loginRequest);

        assertTrue(result.getStatus());
        assertEquals(Messages.USER_AUTHENTICATED, result.getMessage());
        LoginResponse response = (LoginResponse) result.getData();
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    public void testSignup_Failure() {
        User newUser = new User();
        newUser.setEmailAddress("newuser@example.com");
        newUser.setPassword("newUserPassword");

        when(authRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        ActionResult result = authService.register(newUser);

        assertFalse(result.getStatus());
        assertTrue(result.getMessage().contains("Registration failed"));
    }

    @Test
    public void testForgetPassword_Success() throws MessagingException, IOException {
        User mockUser = new User();
        mockUser.setEmailAddress(userEmail);
        mockUser.setPassword("oldPassword123");

        when(authRepository.findByEmailAddress(userEmail)).thenReturn(Optional.of(mockUser));

        try (MockedStatic<VerificationCodeGenerator> mockedStatic = mockStatic(VerificationCodeGenerator.class)) {
            mockedStatic.when(VerificationCodeGenerator::generateCode).thenReturn(verificationCode);

            VerificationCode mockVerificationCode = new VerificationCode();
            mockVerificationCode.setEmail(userEmail);
            mockVerificationCode.setVerificationCode(verificationCode);
            mockVerificationCode.setValid(true);

            when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(mockVerificationCode);

            ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);

            doNothing().when(emailService).sendForgetPasswordEmail(emailCaptor.capture(), codeCaptor.capture());

            ActionResult result = authService.forgetPassword(userEmail);

            assertNotNull(result);
            assertTrue(result.getStatus());
            assertEquals(Messages.EMAIL_SEND_SUCCESS, result.getMessage());

            verify(authRepository, times(1)).findByEmailAddress(userEmail);
            verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
            verify(emailService, times(1)).sendForgetPasswordEmail(eq(userEmail), eq(verificationCode));

            assertEquals(userEmail, emailCaptor.getValue());
            assertEquals(verificationCode, codeCaptor.getValue());
        }
    }

    @Test
    public void testRefreshToken_Success() {
        String refreshToken = "valid_refresh_token";
        String accessToken = "valid_access_token";

        RefreshTokenRequest tokenRequest = new RefreshTokenRequest(refreshToken, accessToken);

        User mockUser = new User();
        mockUser.setEmailAddress(userEmail);
        mockUser.setPassword("encodedPassword123");

        when(jwtService.extractEmailAddress(refreshToken)).thenReturn(userEmail);
        when(jwtService.extractEmailAddress(accessToken)).thenReturn(userEmail);
        when(authRepository.findByEmailAddress(userEmail)).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid(refreshToken, userEmail)).thenReturn(true);
        when(jwtService.generateAccessToken(userEmail)).thenReturn(accessToken);

        ActionResult result = authService.tokenRefresh(tokenRequest);

        assertTrue(result.getStatus());
        assertEquals(Messages.TOKEN_REFRESHED, result.getMessage());
        assertEquals(accessToken, result.getData());
    }

    @Test
    public void testForgetPassword_UserNotFound() throws MessagingException, IOException {
        when(authRepository.findByEmailAddress(invalidEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.forgetPassword(invalidEmail));
    }

    @Test
    public void testVerifyRequest_Success() {
        VerificationRequest verificationRequest = new VerificationRequest(userEmail, verificationCode);

        VerificationCode verificationCodeObj = new VerificationCode();
        verificationCodeObj.setEmail(userEmail);
        verificationCodeObj.setVerificationCode(verificationCode);
        verificationCodeObj.setValid(true);

        when(verificationCodeRepository.findByCode(verificationCode)).thenReturn(Optional.of(verificationCodeObj));

        ActionResult result = authService.verifyRequest(verificationRequest);

        assertTrue(result.getStatus());
        assertEquals(Messages.VERIFICATION_SUCCESS, result.getMessage());
    }

    @Test
    public void testVerifyRequest_InvalidCode() {
        VerificationRequest verificationRequest = new VerificationRequest(userEmail, invalidVerificationCode);

        when(verificationCodeRepository.findByCode(invalidVerificationCode)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.verifyRequest(verificationRequest));
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest(userEmail, newPassword, verificationCode);

        VerificationCode mockVerificationCode = new VerificationCode();
        mockVerificationCode.setEmail(userEmail);
        mockVerificationCode.setVerificationCode(verificationCode);
        mockVerificationCode.setValid(true);

        when(verificationCodeRepository.findByCode(verificationCode))
                .thenReturn(Optional.of(mockVerificationCode));

        User mockUser = new User();
        mockUser.setEmailAddress(userEmail);
        mockUser.setPassword("oldPassword123");

        when(authRepository.findByEmailAddress(userEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword123");

        ActionResult result = authService.resetPassword(request);

        assertNotNull(result);
        assertTrue(result.getStatus());
        assertEquals(Messages.PASSWORD_RESET_SUCCESS, result.getMessage());

        verify(verificationCodeRepository, times(1)).findByCode(verificationCode);
        verify(authRepository, times(1)).findByEmailAddress(userEmail);
        verify(authRepository, times(1)).save(mockUser);

        assertEquals("encodedNewPassword123", mockUser.getPassword());
    }

    @Test
    void testResetPassword_UserNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest(invalidEmail, newPassword, verificationCode);

        VerificationCode mockVerificationCode = new VerificationCode();
        mockVerificationCode.setEmail(invalidEmail);
        mockVerificationCode.setVerificationCode(verificationCode);
        mockVerificationCode.setValid(true);

        when(verificationCodeRepository.findByCode(verificationCode))
                .thenReturn(Optional.of(mockVerificationCode));

        when(authRepository.findByEmailAddress(invalidEmail)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.resetPassword(request);
        });

        assertEquals(Messages.USER_NOT_FOUND, exception.getMessage());

        verify(verificationCodeRepository, times(1)).findByCode(verificationCode);
        verify(authRepository, times(1)).findByEmailAddress(invalidEmail);
        verify(authRepository, never()).save(any());
    }
}
