package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.convert.DurationUnit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    private LocalDateTime startTime;

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

    public Appointment(LocalDateTime startTime, long duration, AppointmentGroup appointmentGroup) {
        this.startTime = startTime;
        this.duration = duration;
        this.appointmentGroup = appointmentGroup;
    }
}
