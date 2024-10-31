package com.auth.auth.controller;

import com.auth.auth.dto.SignupDTO;
import com.auth.auth.model.User;
import com.auth.auth.service.AuthService;
import com.auth.auth.service.JwtService;
import com.auth.auth.utils.ActionResult;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<ActionResult> register(@RequestBody @Valid SignupDTO userData) {
        var user = modelMapper.map(userData, User.class);
        var result = authService.createUser(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
