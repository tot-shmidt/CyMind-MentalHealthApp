package cymind.dto;

import cymind.model.Student;
import jakarta.validation.constraints.NotNull;

public record StudentDTO(String major, int yearOfStudy, @NotNull Long userId) {
    public StudentDTO(Student student) {
        this(student.getMajor(), student.getYearOfStudy(), student.getAbstractUser().getId());
    }
}
