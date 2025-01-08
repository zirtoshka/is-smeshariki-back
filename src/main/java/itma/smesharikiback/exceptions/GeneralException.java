package itma.smesharikiback.exceptions;

import org.springframework.http.HttpStatus;

public class GeneralException extends RuntimeException {
    public HttpStatus httpStatus;

    public GeneralException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

