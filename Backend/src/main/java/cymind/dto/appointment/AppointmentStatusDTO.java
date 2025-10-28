package cymind.dto.appointment;

import cymind.enums.AppointmentStatus;
import cymind.model.Appointment;

public record AppointmentStatusDTO(long id, AppointmentStatus status) {
    public AppointmentStatusDTO(Appointment appointment) {
        this(appointment.getId(), appointment.getStatus());
    }
}
