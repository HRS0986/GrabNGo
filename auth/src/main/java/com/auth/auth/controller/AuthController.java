package com.auth.auth.controller;

import com.auth.auth.dto.LoginRequest;
import com.auth.auth.dto.SignupDTO;
import com.auth.auth.enums.UserRole;
import com.auth.auth.model.User;
import com.auth.auth.service.AuthService;
import com.auth.auth.utils.ActionResult;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ActionResult> register(@RequestBody @Valid SignupDTO userData) {
        var user = modelMapper.map(userData, User.class);
        user.setRole(UserRole.STANDARD_USER);
        var result = authService.createUser(user);
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

}
