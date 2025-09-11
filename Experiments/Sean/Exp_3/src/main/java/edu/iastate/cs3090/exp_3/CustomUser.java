package edu.iastate.cs3090.exp_3;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CustomUser {
    @NotBlank @Email private String email;
    @NotBlank private String passwordHash;
    @Setter private String message;
}
