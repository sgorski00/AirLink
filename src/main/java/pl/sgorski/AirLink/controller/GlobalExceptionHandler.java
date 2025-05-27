package pl.sgorski.AirLink.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(500),
                e.getMessage()
        );
    }
}
