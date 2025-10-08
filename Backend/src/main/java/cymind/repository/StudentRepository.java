package cymind.repository;

import cymind.model.AbstractUser;
import cymind.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findById(long id);
    Student findByAbstractUser(AbstractUser abstractUser);
}
