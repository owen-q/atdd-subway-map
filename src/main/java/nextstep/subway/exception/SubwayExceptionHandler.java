package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateException exception){
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}