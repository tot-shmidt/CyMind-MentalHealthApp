package coms309;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record User(
    @NotNull String name,
    @Email String email,
    String phone
) {}