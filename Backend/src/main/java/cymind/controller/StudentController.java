package cymind.controller;

import cymind.dto.StudentDTO;
import cymind.service.StudentService;
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
    StudentService studentService;

    @PostMapping("/users/student")
    ResponseEntity<StudentDTO> registerStudent(@RequestBody @Valid StudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.addStudentToUser(studentDTO), HttpStatus.CREATED);
    }
}
