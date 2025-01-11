package itma.smesharikiback.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class GeneralException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, String> errors;

    public GeneralException(HttpStatus status, Map<String, String> errors) {
        super("Ошибка валидации");
        this.status = status;
        this.errors = errors;
    }

}

