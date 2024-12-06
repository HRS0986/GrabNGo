package com.auth.auth.service;

import com.auth.auth.constants.Messages;
import com.auth.auth.dto.ChangePasswordRequest;
import com.auth.auth.dto.UserDTO;
import com.auth.auth.enums.UserRole;
import com.auth.auth.exception.UserNotFoundException;
import com.auth.auth.model.User;
import com.auth.auth.repository.AuthRepository;
import com.auth.auth.utils.ActionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserManagerServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserManagerService userManagerService;

    private final String testEmail = "user@example.com";
    private final String oldPassword = "oldPass123";
    private final String newPassword = "newPass123";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        User mockUser = new User();
        mockUser.setEmailAddress(testEmail);
        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.of(mockUser));

        var userDetails = userManagerService.loadUserByUsername(testEmail);

        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        verify(authRepository, times(1)).findByEmailAddress(testEmail);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userManagerService.loadUserByUsername(testEmail));
        verify(authRepository, times(1)).findByEmailAddress(testEmail);
    }

    @Test
    void testChangePassword_Success() {
        User mockUser = new User();
        mockUser.setEmailAddress(testEmail);
        mockUser.setPassword(passwordEncoder.encode(oldPassword));

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, testEmail);

        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(true);

        ActionResult result = userManagerService.changePassword(request);

        assertTrue(result.getStatus());
        assertEquals(Messages.PASSWORD_CHANGED_SUCCESS, result.getMessage());
        verify(authRepository, times(1)).save(mockUser);
    }

    @Test
    void testChangePassword_UserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, testEmail);
        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userManagerService.changePassword(request));
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        User mockUser = new User();
        mockUser.setEmailAddress(testEmail);
        mockUser.setPassword(passwordEncoder.encode("anotherPassword"));

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, testEmail);

        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userManagerService.changePassword(request));
    }

    @Test
    void testGetProfile_Success() {
        User mockUser = new User();
        mockUser.setEmailAddress(testEmail);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmailAddress(testEmail);

        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(mockUser, UserDTO.class)).thenReturn(userDTO);

        ActionResult result = userManagerService.getProfile(testEmail);

        assertTrue(result.getStatus());
        assertEquals(Messages.USER_FOUND, result.getMessage());
        assertEquals(userDTO, result.getData());
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userManagerService.getProfile(testEmail));
    }

    @Test
    void testUpdateProfile_Success() {
        User mockUser = new User();
        mockUser.setEmailAddress(testEmail);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmailAddress(testEmail);

        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.of(mockUser));

        ActionResult result = userManagerService.updateProfile(userDTO);

        assertTrue(result.getStatus());
        assertEquals(Messages.USER_UPDATED_SUCCESS, result.getMessage());
        verify(authRepository, times(1)).save(mockUser);
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmailAddress(testEmail);

        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userManagerService.updateProfile(userDTO));
    }

    @Test
    void testDeleteProfile_Success() {
        User mockUser = new User();
        mockUser.setEmailAddress(testEmail);

        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.of(mockUser));

        ActionResult result = userManagerService.deleteUser(testEmail);

        assertTrue(result.getStatus());
        assertEquals(Messages.USER_DELETED_SUCCESS, result.getMessage());
        assertFalse(mockUser.isActive());
        verify(authRepository, times(1)).save(mockUser);
    }

    @Test
    void testDeleteProfile_UserNotFound() {
        when(authRepository.findByEmailAddress(testEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userManagerService.deleteUser(testEmail));
    }

    @Test
    void testUpdateProfile_OnlyContactNumberChanged() {
        User existingUser = new User(1, "user@example.com", "John", "Doe", "hashed_password", "0712345678", "991234567V", "123 Main St", UserRole.STANDARD_USER, true);
        UserDTO updatedUserDTO = new UserDTO(1, "user@example.com", "John", "Doe", "0711234567", "991234567V", "123 Main St", UserRole.STANDARD_USER);

        when(authRepository.findByEmailAddress("user@example.com")).thenReturn(Optional.of(existingUser));

        ActionResult result = userManagerService.updateProfile(updatedUserDTO);

        assertTrue(result.getStatus());
        assertEquals(Messages.USER_UPDATED_SUCCESS, result.getMessage());
        verify(authRepository).save(argThat(user -> "0711234567".equals(user.getContactNumber()) && "John".equals(user.getFirstName())));
    }

    @Test
    void testUpdateProfile_InvalidEmail() {
        UserDTO invalidUserDTO = new UserDTO(1, "invalid-email", "John", "Doe", "0711234567", "991234567V", "123 Main St", UserRole.STANDARD_USER);
        Exception exception = assertThrows(RuntimeException.class, () -> userManagerService.updateProfile(invalidUserDTO));
        assertEquals(Messages.INVALID_EMAIL, exception.getMessage());
    }

    @Test
    void testUpdateProfile_MissingFirstName() {
        UserDTO invalidUserDTO = new UserDTO(1, "john.doe@example.com", "", "Doe", "0711234567", "991234567V", "123 Main St", UserRole.STANDARD_USER);

        Exception exception = assertThrows(RuntimeException.class, () -> userManagerService.updateProfile(invalidUserDTO));
        assertEquals(Messages.FIRSTNAME_REQUIRED, exception.getMessage());
    }

    @Test
    void testChangePassword_IncorrectCurrentPassword() {
        String userEmail = "john.doe@example.com";
        String incorrectCurrentPassword = "wrongPassword";
        String newPassword = "newSecurePassword";

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                incorrectCurrentPassword,
                newPassword,
                userEmail
        );

        User existingUser = new User(1, userEmail, "John", "Doe", "encoded-correct-password", "0711234567", "991234567V", "123 Main St", UserRole.STANDARD_USER, true);
        when(authRepository.findByEmailAddress(userEmail)).thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches(incorrectCurrentPassword, existingUser.getPassword())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                userManagerService.changePassword(changePasswordRequest)
        );
        assertEquals(Messages.INVALID_PASSWORD, exception.getMessage());

        verify(authRepository, never()).save(any(User.class));
    }
}
