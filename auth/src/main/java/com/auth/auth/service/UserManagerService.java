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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
public class UserManagerService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> credentials = authRepository.findByEmailAddress(username);
        return credentials.map(UserAuthDetails::new).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }

    public ActionResult changePassword(ChangePasswordRequest changePasswordRequest) {
        var isUserActive = isActiveEmail(changePasswordRequest.getEmail());
        User user = (User) isUserActive.getData();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new RuntimeException(Messages.INVALID_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        authRepository.save(user);
        return new ActionResult(true, Messages.PASSWORD_CHANGED_SUCCESS, null, null);
    }

    public ActionResult isActiveEmail(String email) {
        var userOptional = authRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }
        var user = userOptional.get();
        if (!user.isActive()) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }
        return new ActionResult(true, Messages.USER_FOUND, user, null);
    }

    public ActionResult getProfile(String email) {
        var isUserActive = isActiveEmail(email);
        User user = (User) isUserActive.getData();
        var userDTO = modelMapper.map(user, UserDTO.class);
        return new ActionResult(true, Messages.USER_FOUND, userDTO, null);
    }

    public ActionResult isDuplicateUser(String email, String nic) {
        var userOptional = authRepository.findByEmailAddress(email);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            if (user.isActive()) {
                return new ActionResult(true, Messages.EMAIL_ALREADY_EXISTS, null, null);
            }
        }
        userOptional = authRepository.findByNic(nic);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            if (user.isActive()) {
                return new ActionResult(true, Messages.NIC_ALREADY_EXISTS, null, null);
            }
        }
        return new ActionResult(false, Messages.USER_NOT_FOUND, null, null);
    }

    public ActionResult updateProfile(UserDTO userDTO) {
        var isUserActive = isActiveEmail(userDTO.getEmailAddress());
        User user = (User) isUserActive.getData();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setContactNumber(userDTO.getContactNumber());
        user.setNic(userDTO.getNic());
        user.setAddress(userDTO.getAddress());
        authRepository.save(user);
        return new ActionResult(true, Messages.USER_UPDATED_SUCCESS, null, null);
    }

    public ActionResult deleteUser(String email) {
        var isUserActive = isActiveEmail(email);
        User user = (User) isUserActive.getData();
        user.setActive(false);
        webClientBuilder.build()
                .delete()
                .uri("http://apigateway/api/v1/cart/user/" + user.getUserId())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        authRepository.save(user);
        return new ActionResult(true, Messages.USER_DELETED_SUCCESS, null, null);
    }
}
