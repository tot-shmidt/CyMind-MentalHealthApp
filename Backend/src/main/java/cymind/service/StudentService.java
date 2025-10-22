package cymind.service;

import cymind.dto.StudentDTO;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.Student;
import cymind.repository.AbstractUserRepository;
import cymind.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    @Transactional
    public StudentDTO addStudentToUser(StudentDTO studentDTO) throws AuthorizationDeniedException {
        checkAuth(studentDTO.userId());

        // Guaranteed to exist at this point
        AbstractUser user = abstractUserRepository.findById(studentDTO.userId().longValue());
        if (user.getUserType() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has registered type");
        }

        Student student = new Student(studentDTO.major(), studentDTO.yearOfStudy(), user);
        user.setUserType(UserType.STUDENT);
        abstractUserRepository.save(user);

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
