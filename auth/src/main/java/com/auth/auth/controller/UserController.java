package com.auth.auth.controller;

import com.auth.auth.dto.ChangePasswordRequest;
import com.auth.auth.dto.UserDTO;
import com.auth.auth.service.UserManagerService;
import com.auth.auth.utils.ActionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserManagerService userManagerService;

    public UserController(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    @PutMapping("/change-password")
    public ResponseEntity<ActionResult> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            var result = userManagerService.changePassword(changePasswordRequest);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(new ActionResult(false, ex.getMessage(), null, null), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<ActionResult> getProfile(@PathVariable String email) {
        var result = userManagerService.getProfile(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<ActionResult> updateProfile(@RequestBody UserDTO userDTO) {
        var result = userManagerService.updateProfile(userDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<ActionResult> deleteProfile(@RequestBody String email) {
        var result = userManagerService.deleteProfile(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
