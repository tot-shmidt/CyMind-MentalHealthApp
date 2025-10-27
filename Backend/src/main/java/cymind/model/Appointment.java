package cymind.model;

import cymind.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private LocalDateTime startTime;

    @DurationUnit(ChronoUnit.MINUTES)
    @NotNull
    private long duration;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "appointment_group_id")
    @NotNull
    private AppointmentGroup appointmentGroup;

    @NotNull
    private AppointmentStatus status;

    private String location;
    private String title;
    private String description;

    public Appointment(LocalDateTime startTime, long duration, AppointmentGroup appointmentGroup) {
        this.startTime = startTime;
        this.duration = duration;
        this.appointmentGroup = appointmentGroup;
        this.status = AppointmentStatus.UPCOMING;
    }
}
