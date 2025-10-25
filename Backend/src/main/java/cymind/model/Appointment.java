package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.convert.DurationUnit;

import java.sql.Time;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Temporal(TemporalType.DATE)
    @NotBlank
    private Date date;

    @Temporal(TemporalType.TIME)
    @NotBlank
    private Time time;

    @DurationUnit(ChronoUnit.MINUTES)
    @NotBlank
    private long duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_group_id")
    @NotBlank
    private AppointmentGroup appointmentGroup;

    private String location;
    private String title;
    private String description;

    public Appointment(Date date, Time time, long duration, AppointmentGroup appointmentGroup) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.appointmentGroup = appointmentGroup;
    }
}
