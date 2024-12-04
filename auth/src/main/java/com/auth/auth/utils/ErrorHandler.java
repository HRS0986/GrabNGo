package com.auth.auth.utils;

import com.auth.auth.constants.Messages;
import com.auth.auth.exception.DataValidationException;
import com.auth.auth.exception.DuplicateUserException;
import com.auth.auth.exception.InvalidAuthenticationException;
import com.auth.auth.exception.UserNotFoundException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ActionResult> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        var result = new ActionResult(false, Messages.VALIDATION_ERROR, null, errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ActionResult> handleUserNotFound(UserNotFoundException ex) {
        var result = new ActionResult(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ActionResult> handleDuplicateUser(DuplicateUserException ex) {
        var result = new ActionResult(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ActionResult> handleInvalidAuthentication(InvalidAuthenticationException ex) {
        var result = new ActionResult(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ActionResult> handleSignatureException(SignatureException ex) {
        var result = new ActionResult(false, Messages.TOKEN_INVALID, null, Messages.TOKEN_INVALID);
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ActionResult> handleDataValidationException(DataValidationException ex) {
        var result = new ActionResult(false, ex.getMessage(), null, ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
