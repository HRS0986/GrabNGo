package com.auth.auth.service;

import com.auth.auth.constants.Messages;
import com.auth.auth.dto.LoginRequest;
import com.auth.auth.dto.LoginResponse;
import com.auth.auth.dto.RefreshTokenRequest;
import com.auth.auth.utils.ActionResult;
import com.auth.auth.model.User;
import com.auth.auth.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public ActionResult login(LoginRequest credentials) {
        var accessToken = jwtService.generateAccessToken(credentials.getEmailAddress());
        var refreshToken = jwtService.generateRefreshToken(credentials.getEmailAddress());
        var tokenResponse = new LoginResponse(accessToken, refreshToken);
        return new ActionResult(true, Messages.USER_AUTHENTICATED, tokenResponse, null);
    }

    public ActionResult register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var result = authRepository.save(user);
        return new ActionResult(true, Messages.USER_CREATED_SUCCESS, result, null);
    }

    public ActionResult tokenRefresh(RefreshTokenRequest tokenRequest) {
        var emailAddress = jwtService.extractEmailAddress(tokenRequest.getRefreshToken());
        if (!emailAddress.isBlank()) {
            var user = authRepository.findByEmailAddress(emailAddress).orElseThrow();
            if (jwtService.isTokenValid(tokenRequest.getRefreshToken(), user.getEmailAddress())) {
                var accessToken = jwtService.generateAccessToken(emailAddress);
                return new ActionResult(true, Messages.TOKEN_REFRESHED, accessToken, null);
            }
        }
        throw new RuntimeException(Messages.TOKEN_INVALID);
    }

    public ActionResult getUserById(int userId) {
        try {
            var result = authRepository.findById(userId);
            return new ActionResult(true, Messages.USER_FOUND, result, null);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null, null);
        }
    }

    public ActionResult getUserByEmail(String email) {
        try {
            var result = authRepository.findByEmailAddress(email);
            return new ActionResult(true, Messages.USER_FOUND, result, null);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null, null);
        }
    }

    public ActionResult getAllUsers() {
        try {
            var result = authRepository.findAll();
            return new ActionResult(true, Messages.USERS_FOUND, result, null);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null, null);
        }
    }

    public ActionResult updateUser(User user) {
        try {
            var result = authRepository.save(user);
            return new ActionResult(true, Messages.USER_UPDATED_SUCCESS, result, null);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null, null);
        }
    }

    public ActionResult deleteUser(int userId) {
        try {
            authRepository.deleteById(userId);
            return new ActionResult(true, Messages.USER_DELETED_SUCCESS, null, null);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null, null);
        }
    }

}
