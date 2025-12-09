package itma.smesharikiback.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class DomainException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, String> errors;

    public DomainException(HttpStatus status, Map<String, String> errors) {
        super("Domain exception");
        this.status = status;
        this.errors = errors;
    }
}












