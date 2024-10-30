package com.auth.auth.service;

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

    public ActionResult createUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            var result = authRepository.save(user);
            return new ActionResult(true,"User created successfully", result);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null);
        }
    }

    public ActionResult getUserById(int userId) {
        try {
            var result = authRepository.findById(userId);
            return new ActionResult(true, "User found", result);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null);
        }
    }

    public ActionResult getUserByEmail(String email) {
        try {
            var result = authRepository.findByEmail(email);
            return new ActionResult(true, "User found", result);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null);
        }
    }

    public ActionResult getAllUsers() {
        try {
            var result = authRepository.findAll();
            return new ActionResult(true, "Users found", result);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null);
        }
    }

    public ActionResult updateUser(User user) {
        try {
            var result = authRepository.save(user);
            return new ActionResult(true, "User updated successfully", result);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null);
        }
    }

    public ActionResult deleteUser(int userId) {
        try {
            authRepository.deleteById(userId);
            return new ActionResult(true, "User deleted successfully", null);
        } catch (Exception e) {
            return new ActionResult(false, e.getMessage(), null);
        }
    }
}
