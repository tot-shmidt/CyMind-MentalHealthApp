package cymind.controller;

import cymind.dto.ErrorMessageDTO;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Thrown by Jakarta validators, like @NotBlank or @Email
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), errors), HttpStatus.BAD_REQUEST);
    }

    // Thrown by Jakarta with @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        List<String> errors = e.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<ErrorMessageDTO> handleNonUniqueResultException(HttpServletRequest req, NonUniqueResultException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccountNotFoundException(HttpServletRequest req, AccountNotFoundException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthorizationDeniedException(HttpServletRequest req, AuthorizationDeniedException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ErrorMessageDTO> handleNoResultException(HttpServletRequest req, NoResultException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorMessageDTO> handleResponseStatusException(HttpServletRequest req, ResponseStatusException e) {
        // getMessage contains HTTP_STATUS//"actual message"//, getReason() contains the actual message
        String message = e.getMessage();
        if (e.getReason() != null) {
            message = e.getReason();
        }
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), List.of(message)), e.getStatusCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthenticationException(HttpServletRequest req, AuthenticationException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentTypeMismatchException(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<>(new ErrorMessageDTO(req.getRequestURI(), e), HttpStatus.BAD_REQUEST);
    }
}