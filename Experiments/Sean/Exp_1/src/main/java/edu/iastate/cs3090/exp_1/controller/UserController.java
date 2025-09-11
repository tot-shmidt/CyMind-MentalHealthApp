package edu.iastate.cs3090.exp_1.controller;

import edu.iastate.cs3090.exp_1.blog.UserManager;
import edu.iastate.cs3090.exp_1.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;
import java.util.HashMap;
import java.util.UUID;

/**
 * GET    /user
 * GET    /user/:id
 * POST   /user
 * PUT    /user
 * DELETE /user/:id
 */
@RestController
public class UserController {
    // CREATE
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        UserManager.addUser(user);
        return user;
    }

    // READ
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable UUID id) throws KeyException {
        return UserManager.getUser(id);
    }

    // UPDATE
    @PutMapping("/user")
    public User updateUser(@Valid @RequestBody User user) throws KeyException {
        UserManager.replaceUser(user);
        return user;
    }

    // DELETE
    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) throws KeyException {
        UserManager.deleteUser(id);
    }

    // LIST
    @GetMapping("/user")
    public HashMap<UUID, User> getAllUsers() {
        return UserManager.getUsers();
    }

    @ExceptionHandler(KeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleKeyException(KeyException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotValidException(Exception e) {
        return "Username field is required";
    }
}
