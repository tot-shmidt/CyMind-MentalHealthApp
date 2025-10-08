package cymind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.boot.context.properties.bind.Name;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class MoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull
    private int moodRating;

    @OneToOne()
    @JoinColumn(name = "student_id")
    @NotNull(message = "Student cannot be empty")
    @JsonIgnore
    private Student student;

    @OneToOne()
    @JoinColumn(name = "journal_id")
    private JournalEntry journalEntry;

    public MoodEntry(int moodRating, Student student, JournalEntry journalEntry) {
        this.date = new Date();
        this.moodRating = moodRating;
        this.student = student;
        this.journalEntry = journalEntry;
    }

    public MoodEntry(int moodRating, Student student) {
        this.date = new Date();
        this.moodRating = moodRating;
        this.student = student;
    }

    public void updateMoodRating(MoodEntry moodEntry) {
        this.moodRating = moodEntry.getMoodRating();
        this.journalEntry = moodEntry.getJournalEntry();
    }
}
