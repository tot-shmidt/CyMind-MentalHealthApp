package cymind.service;

import cymind.dto.StudentDTO;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.Student;
import cymind.repository.AbstractUserRepository;
import cymind.repository.MoodEntryRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    @Autowired
    MoodEntryRepository moodEntryRepository;

    /**
     * Register the User as a Student
     *
     * @param studentDTO
     * @return
     * @throws AuthorizationDeniedException
     */
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

        log.info("Added student to User {}", user.getId());
        return new StudentDTO(studentRepository.save(student));
    }

    @Transactional
    public StudentDTO get(long id) {
        checkAuth(id);

        Student student = studentRepository.findByAbstractUserId(id);
        if (student == null) {
            throw new NoResultException("Student not found");
        }

        return new StudentDTO(student);
    }

    @Transactional
    public StudentDTO update(long id, StudentDTO studentDTO) {
        Student student = studentRepository.findByAbstractUserId(id);
        if (student == null) {
            throw new NoResultException("Student not found");
        }

        checkAuth(studentDTO.userId());

        student.setMajor(studentDTO.major());
        student.setYearOfStudy(studentDTO.yearOfStudy());

        log.info("Updated student {}", student.getId());
        return new StudentDTO(studentRepository.save(student));
    }

    @Transactional
    public void remove(long id) throws AuthorizationDeniedException {
        checkAuth(id);

        Student student = studentRepository.findByAbstractUserId(id);
        if (student == null) {
            throw new NoResultException("Student not found");
        }

        AbstractUser abstractUser = abstractUserRepository.findById(id);
        if (abstractUser == null) {
            throw new NoResultException("User not found");
        }
        abstractUser.setUserType(null);
        abstractUserRepository.save(abstractUser);

        moodEntryRepository.deleteAllByStudent(student);
        studentRepository.deleteById(student.getId());
        log.info("Deleted student {} (user {})", student.getId(), student.getAbstractUser().getId());
    }

    private void checkAuth(long id) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != id) {
            log.warn("User {} attempted to modify user type for User {}", authedUser.getId(), id);
            throw new AuthorizationDeniedException("Attempting to modify a different user");
        }
    }
}
