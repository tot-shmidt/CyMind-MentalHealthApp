package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotBlank
    private Date date;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotBlank
    private MentalHealthProfessional mentalHealthProfessional;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotBlank
    private Student student;

    private String message;

    public Appointment(Date date, MentalHealthProfessional mentalHealthProfessional, Student student) {
        this.date = date;
        this.mentalHealthProfessional = mentalHealthProfessional;
        this.student = student;
    }
}
