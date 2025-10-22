package cymind.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAbstractUserDTO(@NotBlank @Email String email, @NotBlank String password, @NotBlank String firstName, String lastName, int age) { }
