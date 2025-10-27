package cymind.service;

import cymind.enums.AppointmentStatus;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.Appointment;
import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AppointmentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public Appointment create(Appointment appointment) {
        checkAuth(appointment.getAppointmentGroup());

        return appointmentRepository.save(appointment);
    }

    public Appointment checkStatus(long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            throw new NoResultException("No appointment found");
        }

        LocalDateTime endTime = appointment.getStartTime().plusMinutes(appointment.getDuration());
        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
        } else if (appointment.getStartTime().isBefore(appointment.getStartTime()) &&
                appointment.getStartTime().isAfter(endTime)) {
            appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        } else {
            appointment.setStatus(AppointmentStatus.UPCOMING);
        }

        return appointmentRepository.save(appointment);
    }

    private void checkAuth(AppointmentGroup appointmentGroup) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getUserType() == UserType.STUDENT) {
            if (authedUser.getId() != appointmentGroup.getStudent().getAbstractUser().getId()) {
                throw new AuthorizationDeniedException("Attempting to access a group without user");
            }
        } else if (authedUser.getUserType() == UserType.PROFESSIONAL) {
            for (MentalHealthProfessional professional : appointmentGroup.getMentalHealthProfessionals()) {
                if (authedUser.getId() == professional.getAbstractUser().getId()) {
                    // One of the users pass the auth check, so this is a valid request
                    return;
                }
            }

            throw new AuthorizationDeniedException("Attempting to access a group without user");
        } else {
            throw new AuthorizationDeniedException("Invalid user type");
        }
    }
}
