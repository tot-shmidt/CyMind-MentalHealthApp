package cymind.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import jakarta.validation.constraints.Email;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AbstractUserDTO(long id, @Email String email, String firstName, String lastName, int age, UserType userType) {
    public AbstractUserDTO(AbstractUser abstractUser) {
        this(abstractUser.getId(), abstractUser.getEmail(), abstractUser.getFirstName(), abstractUser.getLastName(),
                abstractUser.getAge(), abstractUser.getUserType());
    }
}
