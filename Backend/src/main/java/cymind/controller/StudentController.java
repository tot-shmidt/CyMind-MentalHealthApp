package cymind.controller;

import cymind.dto.user.PublicUserDTO;
import cymind.dto.user.StudentDTO;
import cymind.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {
    @Autowired
    StudentService studentService;

    @PostMapping("/users/student")
    ResponseEntity<StudentDTO> registerStudent(@RequestBody @Valid StudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.addStudentToUser(studentDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users/student")
    ResponseEntity<List<PublicUserDTO>> getAllStudent(@RequestParam(required = false) String name, @RequestParam(required = false) Integer num) {
        if (num == null) {
            num = -1;
        }

        if (name != null && !name.isBlank()) {
            return new ResponseEntity<>(studentService.getAll(name, num), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(studentService.getAll(num), HttpStatus.OK);
        }
    }

    @GetMapping("/users/student/{id}")
    ResponseEntity<?> getStudent(@PathVariable long id) {
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
