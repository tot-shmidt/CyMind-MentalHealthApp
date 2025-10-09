package cymind.repository;

import cymind.model.MoodEntry;
import cymind.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    MoodEntry findById(long id);
    void deleteById(long id);
    List<MoodEntry> findAllByStudentOrderByIdDesc(Student student);
    void deleteAllByStudent(Student stent);
}
