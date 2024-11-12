package com.auth.auth.service;

import com.auth.auth.constants.Messages;
import com.auth.auth.dto.*;
import com.auth.auth.model.VerificationCode;
import com.auth.auth.repository.VerificationCodeRepository;
import com.auth.auth.utils.ActionResult;
import com.auth.auth.model.User;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.utils.VerificationCodeGenerator;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${webapp.forget-password.url}")
    private String forgetPasswordUrl;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService, VerificationCodeRepository verificationCodeRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
    }

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

    public ActionResult forgetPassword(String email) throws MessagingException, IOException {
        var userOptional = authRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException(Messages.USER_NOT_FOUND);
        }

        String code = VerificationCodeGenerator.generateCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setVerificationCode(code);
        verificationCodeRepository.save(verificationCode);
        emailService.sendForgetPasswordEmail(email, code, forgetPasswordUrl);
        return new ActionResult(true, Messages.EMAIL_SEND_SUCCESS, null, null);
    }

    public ActionResult verifyRequest(VerificationRequest verificationRequest) {
        return verifyRequest(verificationRequest, false);
    }

    public ActionResult verifyRequest(VerificationRequest verificationRequest, boolean deactivateCode) {
        var codeOptional = verificationCodeRepository.findByCode(verificationRequest.getVerificationCode());
        if (codeOptional.isEmpty()) {
            throw new RuntimeException(Messages.INVALID_VERIFICATION_CODE);
        }

        VerificationCode verificationCode = codeOptional.get();
        if (!verificationCode.getEmail().equals(verificationRequest.getEmail())) {
            throw new RuntimeException(Messages.INVALID_VERIFICATION_CODE);
        }

        if (!verificationCode.isValid()) {
            throw new RuntimeException(Messages.INVALID_VERIFICATION_CODE);
        }

        if (deactivateCode) {
            verificationCode.setValid(false);
            verificationCodeRepository.save(verificationCode);
        }

        return new ActionResult(true, Messages.VERIFICATION_SUCCESS, null, null);
    }

    public ActionResult resetPassword(ResetPasswordRequest resetPasswordRequest) {
        var verificationResult = verifyRequest(
                new VerificationRequest(resetPasswordRequest.getEmail(), resetPasswordRequest.getVerificationCode()),
                true
        );

        if (!verificationResult.getStatus()) {
            return verificationResult;
        }

        var userOptional = authRepository.findByEmailAddress(resetPasswordRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException(Messages.USER_NOT_FOUND);
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        authRepository.save(user);
        return new ActionResult(true, Messages.PASSWORD_RESET_SUCCESS, null, null);
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
