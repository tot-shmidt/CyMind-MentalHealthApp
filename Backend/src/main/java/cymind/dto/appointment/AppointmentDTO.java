package cymind.dto.appointment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import cymind.enums.AppointmentStatus;
import cymind.model.Appointment;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentDTO(Long id, @NotNull LocalDateTime startTime, @NotNull Long duration, @NotNull Long appointmentGroupId,
                             AppointmentStatus status, String location, String title, String description) {
    public AppointmentDTO(Appointment appointment) {
        this(
                appointment.getId(),
                appointment.getStartTime(),
                appointment.getDuration(),
                appointment.getAppointmentGroup().getId(),
                appointment.getStatus(),
                appointment.getLocation(),
                appointment.getTitle(),
                appointment.getDescription()
        );
    }
}
