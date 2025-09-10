package edu.iastate.cs3090.exp_1.controller;

import edu.iastate.cs3090.exp_1.blog.UserManager;
import edu.iastate.cs3090.exp_1.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class UserController {
    // CREATE
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
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
    public User updateUser(@RequestBody User user) throws KeyException {
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
        return UserManager.getAllUsers();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleKeyException(KeyException e) {
        return e.getMessage();
    }
}
