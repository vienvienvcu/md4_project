package ra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class SimpleException extends Exception {
    private final HttpStatus httpStatus;

    public SimpleException(String message, HttpStatus httpStatus)
    {
        super(message);
        this.httpStatus = httpStatus;
    }
}
