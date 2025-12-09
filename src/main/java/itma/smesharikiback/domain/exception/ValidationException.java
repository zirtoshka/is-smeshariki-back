package itma.smesharikiback.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ValidationException extends DomainException {
    public ValidationException(Map<String, String> errors) {
        super(HttpStatus.BAD_REQUEST, errors);
    }
}












