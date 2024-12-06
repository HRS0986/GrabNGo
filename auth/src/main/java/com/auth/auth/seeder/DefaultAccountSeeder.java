package com.auth.auth.seeder;

import com.auth.auth.enums.UserRole;
import com.auth.auth.model.User;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DefaultAccountSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private AuthRepository authRepository;

    public DefaultAccountSeeder(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (authRepository.count() == 0) {
            // Seed initial data
//            User user1 = new User("John Doe", "john.doe@example.com", );
//            User user2 = new User("Jane Smith", "jane.smith@example.com");
            User user = new User(
                    1,                          // userId
                    "admin@gmail.com",     // emailAddress
                    "Admin",                     // firstName
                    "User",                      // lastName
                    "123",              // password
                    "+94771234567",             // contactNumber
                    "123456789V",               // nic
                    "123 Main Street",          // address
                    UserRole.ADMINISTRATOR,              // role
                    true                        // isActive
            );
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            authRepository.save(user);

            System.out.println("Seeded initial user data");
        }


    }}
