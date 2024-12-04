package com.auth.auth.service;
import com.auth.auth.constants.Messages;
import com.auth.auth.dto.ChangePasswordRequest;
import com.auth.auth.dto.UserDTO;
import com.auth.auth.exception.UserNotFoundException;
import com.auth.auth.model.User;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.utils.ActionResult;
import com.auth.auth.utils.UserAuthDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserManagerService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> credentials = authRepository.findByEmailAddress(username);
        return credentials.map(UserAuthDetails::new).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }

    public ActionResult changePassword(ChangePasswordRequest changePasswordRequest) {
        var userOptional = authRepository.findByEmailAddress(changePasswordRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException(Messages.USER_NOT_FOUND);
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new RuntimeException(Messages.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        authRepository.save(user);
        return new ActionResult(true, Messages.PASSWORD_CHANGED_SUCCESS, null, null);
    }

    public ActionResult getProfile(String email) {
        var userOptional = authRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }

        var user = userOptional.get();
        var userDTO = modelMapper.map(user, UserDTO.class);
        return new ActionResult(true, Messages.USER_FOUND, userDTO, null);
    }

    public ActionResult updateProfile(UserDTO userDTO) {
        var userOptional = authRepository.findByEmailAddress(userDTO.getEmailAddress());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }

        var user = userOptional.get();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setContactNumber(userDTO.getContactNumber());
        user.setNic(userDTO.getNic());
        user.setAddress(userDTO.getAddress());
        authRepository.save(user);
        return new ActionResult(true, Messages.USER_UPDATED_SUCCESS, null, null);
    }

    public ActionResult deleteProfile(String email) {
        var userOptional = authRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }
        var user = userOptional.get();
        user.setActive(false);
        authRepository.save(user);
        return new ActionResult(true, Messages.USER_DELETED_SUCCESS, null, null);
    }
}
