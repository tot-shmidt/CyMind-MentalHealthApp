package cymind.dto;

import cymind.model.Student;

public record StudentDTO(String major, int yearOfStudy, long userId) {
    public StudentDTO(Student student) {
        this(student.getMajor(), student.getYearOfStudy(), student.getAbstractUser().getId());
    }
}
