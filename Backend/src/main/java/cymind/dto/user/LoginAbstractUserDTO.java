package cymind.dto.user;

import jakarta.validation.constraints.Email;

public record LoginAbstractUserDTO(@Email String email, String password) {
}
