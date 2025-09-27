package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.Name;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class MoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date date;

    @NotNull
    private int moodRating;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<JournalEntry> journalEntries;

    public MoodEntry(int moodRating, List<JournalEntry> entries) {
        this.date = new Date();
        this.moodRating = moodRating;
        this.journalEntries = entries;
    }

    public MoodEntry(int moodRating, JournalEntry entry) {
        this.date = new Date();
        this.moodRating = moodRating;
        this.journalEntries = List.of(entry);
    }

    public MoodEntry(int moodRating) {
        this.date = new Date();
        this.moodRating = moodRating;
    }

    public MoodEntry() {
        this.date = new Date();
        this.moodRating = 0;
    }


    public void addJournalEntry(JournalEntry entry) {
        this.journalEntries.add(entry);
    }
}
