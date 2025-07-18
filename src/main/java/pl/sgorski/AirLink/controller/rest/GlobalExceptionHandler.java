package pl.sgorski.AirLink.controller.rest;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.sgorski.AirLink.exception.IllegalStatusException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class, AuthenticationException.class})
    public ProblemDetail handleUsernameNotFoundException(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ProblemDetail handleIllegalArgumentException(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail handlePropertyReferenceException(PropertyReferenceException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid sorting property: " + e.getPropertyName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(IllegalStatusException.class)
    public ProblemDetail handleIllegalStatusException(IllegalStatusException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccessException(DataAccessException e) {
        if (e instanceof DataIntegrityViolationException ie) {
            return ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    "Data integrity violation: " + ie.getMostSpecificCause().getMessage()
            );
        }
        e.printStackTrace();
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database error: " + e.getMessage()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNotFoundException(NoSuchElementException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        e.printStackTrace();
        return ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(500),
                e.getMessage()
        );
    }
}
