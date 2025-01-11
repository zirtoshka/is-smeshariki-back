package itma.smesharikiback.controllers;

import itma.smesharikiback.exceptions.GeneralException;
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

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(GeneralException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getErrors());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> AuthException(AuthenticationException ignoredE) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Неверный логин или пароль!");
        return ResponseEntity.badRequest().body(errors);
    }


}
