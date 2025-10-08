package cymind.controller;

import cymind.dto.StudentDTO;
import cymind.model.Student;
import cymind.repository.AbstractUserRepository;
import cymind.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AbstractUserRepository abstractUserRepository;

    @PostMapping("/users/student")
    ResponseEntity<Student> registerStudent(@RequestBody @Valid StudentDTO studentDTO) {
        Student student = new Student(studentDTO.major(), studentDTO.yearOfStudy(), abstractUserRepository.findById(studentDTO.userId()));
        return new ResponseEntity<>(studentRepository.save(student), HttpStatus.CREATED);
    }
}
