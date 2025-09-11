package edu.iastate.cs3090.exp_1.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private final UUID id = UUID.randomUUID();
    @NotEmpty(message = "Username is required") private String username;
    @Email private String email;
}
