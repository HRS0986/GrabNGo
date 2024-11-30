package com.auth.auth.service;

import com.auth.auth.constants.Messages;
import com.auth.auth.dto.*;
import com.auth.auth.exception.InvalidAuthenticationException;
import com.auth.auth.exception.UserNotFoundException;
import com.auth.auth.model.User;
import com.auth.auth.model.VerificationCode;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.repository.VerificationCodeRepository;
import com.auth.auth.utils.ActionResult;
import com.auth.auth.utils.VerificationCodeGenerator;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final WebClient.Builder webClientBuilder;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService, EmailService emailService, VerificationCodeRepository verificationCodeRepository, WebClient.Builder webClientBuilder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public ActionResult login(LoginRequest credentials) {
        var accessToken = jwtService.generateAccessToken(credentials.getEmailAddress());
        var refreshToken = jwtService.generateRefreshToken(credentials.getEmailAddress());
        var tokenResponse = new LoginResponse(accessToken, refreshToken);
        return new ActionResult(true, Messages.USER_AUTHENTICATED, tokenResponse, null);
    }

    public ActionResult register(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            var savedUser = authRepository.save(user);
            webClientBuilder.build()
                    .post()
                    .uri("http://apigateway/api/v1/cart")
                    .bodyValue(savedUser.getUserId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            return new ActionResult(
                    true,
                    Messages.USER_CREATED_SUCCESS,
                    savedUser,
                    null
            );

        } catch (WebClientResponseException e) {
            return new ActionResult(
                    false,
                    "Cart creation failed: " + e.getMessage(),
                    null,
                    e.getMessage()
            );
        } catch (Exception e) {
            return new ActionResult(
                    false,
                    "Registration failed: " + e.getMessage(),
                    null,
                    e.getMessage()
            );
        }
    }

    public ActionResult tokenRefresh(RefreshTokenRequest tokenRequest) {
        var emailAddressRefreshToken = jwtService.extractEmailAddress(tokenRequest.getRefreshToken());
        var emailAddressAccessToken = jwtService.extractEmailAddress(tokenRequest.getAccessToken());

        if (!emailAddressRefreshToken.isBlank() && !emailAddressAccessToken.isBlank()) {
            if (emailAddressRefreshToken.equals(emailAddressAccessToken)) {
                var user = authRepository.findByEmailAddress(emailAddressRefreshToken).orElseThrow();
                if (jwtService.isTokenValid(tokenRequest.getRefreshToken(), user.getEmailAddress())) {
                    var accessToken = jwtService.generateAccessToken(emailAddressRefreshToken);
                    return new ActionResult(true, Messages.TOKEN_REFRESHED, accessToken, null);
                }
            }
        }

        throw new InvalidAuthenticationException(Messages.TOKEN_INVALID);
    }

    public ActionResult forgetPassword(String email) throws MessagingException, IOException {
        var userOptional = authRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }
        if (!userOptional.get().isActive()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }

        String code = VerificationCodeGenerator.generateCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setVerificationCode(code);
        verificationCodeRepository.save(verificationCode);
        emailService.sendForgetPasswordEmail(email, code);
        return new ActionResult(true, Messages.EMAIL_SEND_SUCCESS, null, null);
    }

    public ActionResult verifyRequest(VerificationRequest verificationRequest) {
        return verifyRequest(verificationRequest, false);
    }

    public ActionResult verifyRequest(VerificationRequest verificationRequest, boolean deactivateCode) {
        var codeOptional = verificationCodeRepository.findByCode(verificationRequest.getVerificationCode());
        if (codeOptional.isEmpty()) {
            throw new InvalidAuthenticationException(Messages.INVALID_VERIFICATION_CODE);
        }

        VerificationCode verificationCode = codeOptional.get();
        if (!verificationCode.getEmail().equals(verificationRequest.getEmail())) {
            throw new InvalidAuthenticationException(Messages.INVALID_VERIFICATION_CODE);
        }

        if (!verificationCode.isValid()) {
            throw new InvalidAuthenticationException(Messages.INVALID_VERIFICATION_CODE);
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
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }

        if (!userOptional.get().isActive()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        authRepository.save(user);
        return new ActionResult(true, Messages.PASSWORD_RESET_SUCCESS, null, null);
    }
}
