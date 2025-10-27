package cymind.dto.appointment;

import cymind.model.Appointment;
import cymind.model.AppointmentGroup;

import java.util.List;

public record AppointmentGroupDTO(Long id, List<Long> professionalIds, Long studentId, String groupName, List<Appointment> appointments) {
    public AppointmentGroupDTO(AppointmentGroup appointmentGroup) {
        this(
                appointmentGroup.getId(),
                // List of MentalHealthProfessionals -> List of AbstractUser ids
                appointmentGroup.getMentalHealthProfessionals().stream().map(professional -> professional.getAbstractUser().getId()).toList(),
                appointmentGroup.getStudent().getAbstractUser().getId(),
                appointmentGroup.getGroupName(),
                appointmentGroup.getAppointments()
        );
    }
}
