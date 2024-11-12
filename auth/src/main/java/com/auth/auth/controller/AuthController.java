package com.auth.auth.controller;

import com.auth.auth.constants.Messages;
import com.auth.auth.dto.LoginRequest;
import com.auth.auth.dto.RefreshTokenRequest;
import com.auth.auth.dto.SignupDTO;
import com.auth.auth.enums.UserRole;
import com.auth.auth.model.User;
import com.auth.auth.service.AuthService;
import com.auth.auth.utils.ActionResult;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final Validator validator;

    public AuthController(AuthService authService, ModelMapper modelMapper, AuthenticationManager authenticationManager, Validator validator) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.validator = validator;
    }

    @PostMapping("/register")
    public ResponseEntity<ActionResult> register(@RequestBody @Valid SignupDTO userData) {
        var user = modelMapper.map(userData, User.class);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            String errorMessage = Messages.PASSWORD_REQUIRED;
            throw new ConstraintViolationException(errorMessage, violations);
        }
        var result = authService.register(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ActionResult> login(@RequestBody @Valid LoginRequest credentials) {
        var userNamePasswordToken = new UsernamePasswordAuthenticationToken(credentials.getEmailAddress(), credentials.getPassword());
        Authentication authentication = authenticationManager.authenticate(userNamePasswordToken);
        if (authentication.isAuthenticated()) {
            var authResult = authService.login(credentials);
            return new ResponseEntity<>(authResult, HttpStatus.OK);
        }
        throw new RuntimeException("Authentication failed");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ActionResult> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        var result = authService.tokenRefresh(refreshTokenRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
