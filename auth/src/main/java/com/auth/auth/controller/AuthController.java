package com.auth.auth.controller;

import com.auth.auth.dto.SignupDTO;
import com.auth.auth.model.User;
import com.auth.auth.service.AuthService;
import com.auth.auth.service.JwtService;
import com.auth.auth.utils.ApiResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ApiResponse register(@RequestBody SignupDTO userData) {
        try {
            var user = modelMapper.map(userData, User.class);
            var result = authService.createUser(user);
            var response = new ApiResponse(result.getStatus(), HttpStatus.CREATED, result.getMessage(), result.getData());
            if (!result.getStatus()) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
            }
            return response;
        } catch (Exception e) {
            return new ApiResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

}
