package com.auth.auth.service;

import com.auth.auth.model.User;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.utils.UserAuthDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserManagerService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> credentials = authRepository.findByEmailAddress(username);
        return credentials.map(UserAuthDetails::new).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }

}
