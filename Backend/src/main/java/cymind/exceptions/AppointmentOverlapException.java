package cymind.exceptions;

import cymind.dto.errors.OverlappingAppointmentDTO;
import cymind.model.Appointment;
import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;

import java.util.ArrayList;
import java.util.List;

public class AppointmentOverlapException extends RuntimeException {
    private final List<Appointment> appointments;
    private final AppointmentGroup appointmentGroup;
    private final List<Long> professionalIds;

    public AppointmentOverlapException(List<Appointment> appointments, AppointmentGroup appointmentGroup) {
        super("There are appointments that overlap with this scheduled time");

        this.appointments = appointments;
        this.appointmentGroup = appointmentGroup;
        this.professionalIds = appointmentGroup.getMentalHealthProfessionals().stream().map(MentalHealthProfessional::getId).toList();
    }

    public List<OverlappingAppointmentDTO> getOverlapAppointments() {
        List<OverlappingAppointmentDTO> overlapMessages = new ArrayList<>();

        for (Appointment appointment : appointments) {
            Long groupId = null, studentId = null;
            if (appointment.getAppointmentGroup().getId() == appointmentGroup.getId()) {
                groupId = appointment.getAppointmentGroup().getId();
            }

            if (appointment.getAppointmentGroup().getStudent().getId() == appointmentGroup.getStudent().getId()) {
                studentId = appointment.getAppointmentGroup().getStudent().getId();
            }

            List<Long> overlappingProfessionalIds = appointment.getAppointmentGroup().getMentalHealthProfessionals().stream()
                    .map(MentalHealthProfessional::getId)
                    .filter(professionalIds::contains)
                    .toList();

            if (overlappingProfessionalIds.isEmpty()) {
                overlappingProfessionalIds = null;
            }

            overlapMessages.add(new OverlappingAppointmentDTO(groupId, studentId, overlappingProfessionalIds, appointment.getStartTime(), appointment.getStartTime().plusMinutes(appointment.getDuration())));
        }

        return overlapMessages;
    }
}
