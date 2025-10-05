package cymind.dto;

import cymind.model.AbstractUser;
import jakarta.validation.constraints.Email;

public record AbstractUserDTO(long id, @Email String email, String firstName, String lastName, int age) {
    public AbstractUserDTO(AbstractUser abstractUser) {
        this(abstractUser.getId(), abstractUser.getEmail(), abstractUser.getFirstName(), abstractUser.getLastName(), abstractUser.getAge());
    }
}
