package cymind.service;

import cymind.dto.StudentDTO;
import cymind.model.AbstractUser;
import cymind.model.Student;
import cymind.repository.AbstractUserRepository;
import cymind.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    public StudentDTO addStudentToUser(StudentDTO studentDTO) throws AuthorizationDeniedException {
        checkAuth(studentDTO.userId());

        Student student = new Student(studentDTO.major(), studentDTO.yearOfStudy(), abstractUserRepository.findById(studentDTO.userId()));
        return new StudentDTO(studentRepository.save(student));
    }

    private void checkAuth(long id) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != id) {
            log.warn("User {} attempted to modify user type for User {}", authedUser.getId(), id);
            throw new AuthorizationDeniedException("Attempting to modify a different user");
        }
    }
}
