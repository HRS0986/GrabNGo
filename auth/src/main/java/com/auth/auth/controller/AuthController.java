package com.auth.auth.controller;
import com.auth.auth.constants.Messages;
import com.auth.auth.dto.*;
import com.auth.auth.exception.DataValidationException;
import com.auth.auth.exception.DuplicateUserException;
import com.auth.auth.exception.InvalidAuthenticationException;
import com.auth.auth.model.User;
import com.auth.auth.service.AuthService;
import com.auth.auth.service.EmailService;
import com.auth.auth.service.UserManagerService;
import com.auth.auth.utils.ActionResult;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final Validator validator;
    private final UserManagerService userManagerService;

    public AuthController(AuthService authService, ModelMapper modelMapper, AuthenticationManager authenticationManager, Validator validator, EmailService emailService, UserManagerService userManagerService) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.validator = validator;
        this.userManagerService = userManagerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ActionResult> register(@RequestBody @Valid SignupDTO userData) {
        var user = modelMapper.map(userData, User.class);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        var existingUserResult = userManagerService.isDuplicateUser(userData.getEmailAddress(), userData.getNic());
        if (existingUserResult.getStatus()) {
            throw new DuplicateUserException(existingUserResult.getMessage());
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            String errorMessage = Messages.PASSWORD_REQUIRED;
            throw new ConstraintViolationException(errorMessage, violations);
        }
        var result = authService.register(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ActionResult> login(@RequestBody LoginRequest credentials) {
        var userNamePasswordToken = new UsernamePasswordAuthenticationToken(credentials.getEmailAddress(), credentials.getPassword());
        Authentication authentication = authenticationManager.authenticate(userNamePasswordToken);
        var isActiveEmail = userManagerService.isActiveEmail(credentials.getEmailAddress());
        if (authentication.isAuthenticated() && isActiveEmail.getStatus()) {
            var authResult = authService.login(credentials);
            return new ResponseEntity<>(authResult, HttpStatus.OK);
        }
        throw new InvalidAuthenticationException(Messages.INVALID_LOGIN_ATTEMPT);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ActionResult> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        if (refreshTokenRequest.getAccessToken() == null || refreshTokenRequest.getAccessToken().isEmpty()) {
            throw new DataValidationException(Messages.TOKEN_INVALID);
        }

        if (refreshTokenRequest.getRefreshToken() == null || refreshTokenRequest.getRefreshToken().isEmpty()) {
            throw new DataValidationException(Messages.TOKEN_INVALID);
        }

        var result = authService.tokenRefresh(refreshTokenRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ActionResult> forgetPassword(@RequestBody String email) throws MessagingException, IOException {
        var result = authService.forgetPassword(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ActionResult> verify(@RequestBody VerificationRequest verificationRequest) {
        if (verificationRequest.getVerificationCode() == null || verificationRequest.getVerificationCode().isEmpty()) {
            throw new DataValidationException(Messages.VERIFICATION_CODE_REQUIRED);
        }

        if (verificationRequest.getEmail() == null || verificationRequest.getEmail().isEmpty()) {
            throw new DataValidationException(Messages.EMAIL_REQUIRED);
        }

        var result = authService.verifyRequest(verificationRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ActionResult> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        if (resetPasswordRequest.getEmail() == null || resetPasswordRequest.getEmail().isEmpty()) {
            throw new DataValidationException(Messages.EMAIL_REQUIRED);
        }

        if (resetPasswordRequest.getPassword() == null || resetPasswordRequest.getPassword().isEmpty()) {
            throw new DataValidationException(Messages.PASSWORD_REQUIRED);
        }

        if (resetPasswordRequest.getVerificationCode() == null || resetPasswordRequest.getVerificationCode().isEmpty()) {
            throw new DataValidationException(Messages.VERIFICATION_CODE_REQUIRED);
        }

        var result = authService.resetPassword(resetPasswordRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
