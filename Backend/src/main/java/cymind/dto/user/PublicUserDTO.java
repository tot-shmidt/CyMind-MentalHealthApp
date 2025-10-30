package cymind.dto.user;

import cymind.model.AbstractUser;
import cymind.model.MentalHealthProfessional;
import cymind.model.Student;

public record PublicUserDTO(Long userId, String firstName, String lastName) {
    public PublicUserDTO(MentalHealthProfessional professional) {
        this(
                professional.getAbstractUser().getId(),
                professional.getAbstractUser().getFirstName(),
                professional.getAbstractUser().getLastName()
        );
    }

    public PublicUserDTO(Student student) {
        this(
                student.getAbstractUser().getId(),
                student.getAbstractUser().getFirstName(),
                student.getAbstractUser().getLastName()
        );
    }
}
