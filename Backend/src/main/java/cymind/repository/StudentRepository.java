package cymind.repository;

import cymind.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findById(long id);
    Student findByAbstractUserId(long id);
    List<Student> findAllByAbstractUserIdIn(List<Long> id);
    List<Student> findByAbstractUser_FirstNameContainingAndAbstractUser_LastNameContainingOrderByAbstractUser(String firstName, String lastName);

    @Query("SELECT u FROM Student u WHERE u.abstractUser.firstName LIKE %:name% OR u.abstractUser.lastName LIKE %:name%")
    List<Student> findByName(@Param("name") String name);
}
