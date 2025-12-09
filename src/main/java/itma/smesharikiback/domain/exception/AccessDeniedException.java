package itma.smesharikiback.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class AccessDeniedException extends DomainException {
    public AccessDeniedException(Map<String, String> errors) {
        super(HttpStatus.FORBIDDEN, errors);
    }
}












