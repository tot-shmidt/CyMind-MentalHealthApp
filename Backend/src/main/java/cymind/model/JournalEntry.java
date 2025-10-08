package cymind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull
    private String entryName;

    @NotNull
    private String content;

    @OneToOne()
    @JoinColumn(name = "mood_id")
    @NotNull(message = "Student cannot be empty")
    @JsonIgnore
    private MoodEntry moodEntry;
}
