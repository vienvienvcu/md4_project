package ra.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ra.exception.CustomException;
import ra.exception.FileUploadException;
import ra.exception.SimpleException;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationHandler {
    // validio dau vao
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
                new SimpleResponse(errors, HttpStatus.BAD_REQUEST)
        );
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex)
    {
        return new ResponseEntity<>(
                new DataResponse<>(ex.getMessage(), ex.getStatus()),
                ex.getStatus()
        );
    }
    @ExceptionHandler(SimpleException.class)
    public ResponseEntity<?> handleSimpleException(SimpleException ex)
    {
        return new ResponseEntity<>(
                new SimpleResponse(ex.getMessage(), ex.getHttpStatus()),
                ex.getHttpStatus()
        );
    }


    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<?> handleFileUploadException(FileUploadException ex) {
        return new ResponseEntity<>(
                new DataResponse(ex.getMessage(), HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

}
