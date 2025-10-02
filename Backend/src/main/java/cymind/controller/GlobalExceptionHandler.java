package cymind.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.NonUniqueResultException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<ObjectNode> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(violation -> {
            ObjectNode node = mapper.createObjectNode();
            node.put("entity", violation.getPropertyPath().toString());
            node.put("message", violation.getMessage());
            errors.add(node);
        });

        return new ResponseEntity<>(mapper.writeValueAsString(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<ObjectNode> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            ObjectNode node = mapper.createObjectNode();
            node.put("entity", error.getObjectName());
            node.put("message", error.getDefaultMessage());
            errors.add(node);
        });

        return new ResponseEntity<>(mapper.writeValueAsString(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<String> handleNonUniqueResultException(NonUniqueResultException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<ObjectNode> errors = new ArrayList<>();
        ObjectNode node = mapper.createObjectNode();
        node.put("entity", "email");
        node.put("message", e.getMessage());
        errors.add(node);

        return new ResponseEntity<>(mapper.writeValueAsString(errors), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<ObjectNode> errors = new ArrayList<>();
        ObjectNode node = mapper.createObjectNode();
        node.put("entity", "id");
        node.put("message", e.getMessage());
        errors.add(node);

        return new ResponseEntity<>(mapper.writeValueAsString(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAuthorizationDeniedException(AuthorizationDeniedException e) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<ObjectNode> errors = new ArrayList<>();
        ObjectNode node = mapper.createObjectNode();
        node.put("entity", "auth");
        node.put("message", e.getMessage());
        errors.add(node);

        return new ResponseEntity<>(mapper.writeValueAsString(errors), HttpStatus.UNAUTHORIZED);
    }
}