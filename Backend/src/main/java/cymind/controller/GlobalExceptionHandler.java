package cymind.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cymind.dto.ErrorMessageDTO;
import jakarta.persistence.NonUniqueResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
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
}