package cymind.service;

import cymind.dto.user.PublicUserDTO;
import cymind.dto.user.StudentDTO;
import cymind.enums.UserType;
import cymind.exceptions.AuthorizationDeniedException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public List<PublicUserDTO> getAll(int num) {
        List<PublicUserDTO> students = studentRepository.findAll().stream()
                .map(PublicUserDTO::new)
                .toList();

        if (num > 0) {
            return students.subList(0, Math.min(num, students.size()));
        } else {
            return students;
        }
    }

    @Transactional
    public List<PublicUserDTO> getAll(String name, int num) {
        List<Student> students;
        String[] nameParts = name.split(" ");
        if (nameParts.length > 1) {
            String firstName = nameParts[0];
            String lastName = nameParts[1];
            students = studentRepository.findByAbstractUser_FirstNameContainingAndAbstractUser_LastNameContainingOrderByAbstractUser(firstName, lastName);
        } else {
            students = studentRepository.findByName(name);
        }

        List<PublicUserDTO> publicDTOs = students.stream()
                .map(PublicUserDTO::new)
                .toList();
        if (num > 0) {
            return publicDTOs.subList(0, Math.min(num, publicDTOs.size()));
        } else {
            return publicDTOs;
        }
    }

    @Transactional
    public Record get(long id) {
        Student student = studentRepository.findByAbstractUserId(id);
        if (student == null) {
            throw new NoResultException("Student not found");
        }

        try {
            checkAuth(id);
            return new StudentDTO(student);
        } catch (AuthorizationDeniedException e) {
            return new PublicUserDTO(student);
        }
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
