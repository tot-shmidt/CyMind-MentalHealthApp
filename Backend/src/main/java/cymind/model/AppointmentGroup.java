package cymind.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private List<MentalHealthProfessional> professionalList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotBlank
    private Student student;

    private String groupName;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "appointmentGroup")
    @Nullable
    private List<Appointment> appointment;

    public AppointmentGroup(List<MentalHealthProfessional> professionalList, Student student) {
        this.professionalList = professionalList;
        this.student = student;
    }

    public AppointmentGroup(MentalHealthProfessional professional, Student student) {
        this.professionalList = List.of(professional);
        this.student = student;
    }
}
