package cymind.controller;

import cymind.dto.StudentDTO;
import cymind.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {
    @Autowired
    StudentService studentService;

    @PostMapping("/users/student")
    ResponseEntity<StudentDTO> registerStudent(@RequestBody @Valid StudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.addStudentToUser(studentDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users/student/{id}")
    ResponseEntity<StudentDTO> getStudent(@PathVariable long id) {
        return new ResponseEntity<>(studentService.get(id), HttpStatus.OK);
    }

    @PutMapping("/users/student/{id}")
    ResponseEntity<StudentDTO> updateStudent(@PathVariable long id, @RequestBody StudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.update(id, studentDTO), HttpStatus.OK);
    }

    @DeleteMapping("/users/student/{id}")
    ResponseEntity<?> deleteStudent(@PathVariable long id) {
        studentService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
