package cymind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ChatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "professional_id"))
    @NotNull
    List<MentalHealthProfessional> professionals;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "student_id"))
    @NotNull
    List<Student> students;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "chatGroup")
    @JsonIgnore
    @Nullable
    List<ChatMessage> chatMessages;

    @Temporal(TemporalType.DATE)
    Date createdOn = new Date();

    String groupName;

    public ChatGroup(List<MentalHealthProfessional> professionals, List<Student> students, String groupName) {
        this.professionals = professionals;
        this.students = students;
        this.groupName = groupName;
    }
}
