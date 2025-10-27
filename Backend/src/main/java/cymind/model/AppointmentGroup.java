package cymind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class AppointmentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "professional_id"))
    @NotNull
    private List<MentalHealthProfessional> mentalHealthProfessionals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotNull
    private Student student;

    private String groupName;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "appointmentGroup")
    @JsonIgnore
    @Nullable
    private List<Appointment> appointments;

    public AppointmentGroup(List<MentalHealthProfessional> professionals, Student student, String groupName) {
        this.mentalHealthProfessionals = professionals;
        this.student = student;
        this.groupName = groupName;
    }
}
