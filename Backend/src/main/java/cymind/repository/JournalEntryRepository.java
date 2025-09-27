package cymind.repository;

import cymind.model.JournalEntry;
import cymind.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    JournalEntry findById(long id);
    void deleteById(long id);
}
