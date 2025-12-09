package itma.smesharikiback.presentation.controller;

import io.jsonwebtoken.JwtException;
import itma.smesharikiback.domain.exception.AccessDeniedException;
import itma.smesharikiback.domain.exception.DomainException;
import itma.smesharikiback.domain.exception.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({DomainException.class, ValidationException.class, AccessDeniedException.class})
    public ResponseEntity<Map<String, String>> handleGeneralException(DomainException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getErrors());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> authException(AuthenticationException ignoredE) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Неверный логин или пароль!");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> jwtException(JwtException ignoredE) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Неверный токен!");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Неверный запрос!");
        return ResponseEntity.badRequest().body(errors);
    }


}













