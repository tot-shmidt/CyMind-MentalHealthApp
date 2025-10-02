package cymind.dto;

import cymind.model.AbstractUser;

public record AbstractUserDTO(String email, String firstName, String lastName, int age) {
    public AbstractUserDTO(AbstractUser abstractUser) {
        this(abstractUser.getEmail(), abstractUser.getFirstName(), abstractUser.getLastName(), abstractUser.getAge());
    }
}
