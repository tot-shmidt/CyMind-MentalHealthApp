package cymind.dto;

import jakarta.validation.constraints.Email;

public record LoginAbstractUserDTO(@Email String email, String password) {
}
