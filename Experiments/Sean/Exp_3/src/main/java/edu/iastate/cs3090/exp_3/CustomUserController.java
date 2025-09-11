package edu.iastate.cs3090.exp_3;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.NoSuchElementException;

@RestController
public class CustomUserController {
    private final HashMap<String, CustomUser> users = new HashMap<String, CustomUser>();

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public CustomUserController(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user/{email}")
    public CustomUser getUser(@PathVariable String email, Authentication auth) throws AuthorizationDeniedException {
        CustomUser customUser = users.get(email);

        String authenticatedUsername = ((UserDetails) auth.getPrincipal()).getUsername();
        if (customUser.getEmail().equals(authenticatedUsername)) {
            return customUser;
        } else {
            throw new AuthorizationDeniedException("User with email: " + email + " not found");
        }
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomUser createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        String encodedPassword = passwordEncoder.encode(createUserRequest.password());

        userDetailsManager.createUser(
                User.withUsername(createUserRequest.email())
                        .password(encodedPassword)
                        .roles("USER")
                        .build()
        );

        CustomUser newUser = new CustomUser(createUserRequest.email(), encodedPassword, createUserRequest.message());
        users.put(newUser.getEmail(), newUser);

        return newUser;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAuthorizationDeniedException(Exception e) {
        return e.getMessage();
    }

    public record CreateUserRequest(@NotBlank @Email String email, @NotBlank String password, String message) { }
}
