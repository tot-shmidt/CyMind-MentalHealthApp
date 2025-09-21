package cymind.repository;

import cymind.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    MoodEntry findById(long id);
    void deleteById(long id);
}
