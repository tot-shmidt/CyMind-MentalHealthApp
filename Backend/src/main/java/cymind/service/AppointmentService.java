package cymind.service;

import cymind.dto.appointment.AppointmentDTO;
import cymind.dto.appointment.AppointmentStatusDTO;
import cymind.enums.AppointmentStatus;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.Appointment;
import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AppointmentGroupRepository;
import cymind.repository.AppointmentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentGroupRepository appointmentGroupRepository;

    @Transactional
    public AppointmentDTO create(AppointmentDTO appointmentDTO) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(appointmentDTO.appointmentGroupId().longValue());
        if (appointmentGroup == null) {
            throw new NoResultException("No appointment group found");
        }

        checkAuth(appointmentGroup);

        Appointment appointment = new Appointment(appointmentDTO.startTime(), appointmentDTO.duration(), appointmentGroup);
        appointment.setLocation(appointmentDTO.location());
        appointment.setDescription(appointmentDTO.description());
        appointment.setTitle(appointmentDTO.title());

        return new AppointmentDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO get(long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            throw new NoResultException("No appointment found");
        }

        checkAuth(appointment.getAppointmentGroup());
        appointment = checkStatus(appointment);

        return new AppointmentDTO(appointment);
    }

    @Transactional
    public AppointmentDTO update(long id, AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            throw new NoResultException("No appointment found");
        }

        checkAuth(appointment.getAppointmentGroup());

        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);
        if (appointmentGroup == null) {
            throw new NoResultException("No appointment group found");
        }

        appointment = checkStatus(appointment);
        appointment.setAppointmentGroup(appointmentGroup);
        appointment.setStartTime(appointmentDTO.startTime());
        appointment.setDuration(appointmentDTO.duration());
        appointment.setLocation(appointmentDTO.location());
        appointment.setDescription(appointmentDTO.description());
        appointment.setTitle(appointmentDTO.title());

        return new AppointmentDTO(appointmentRepository.save(appointment));
    }

    public void delete(long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            throw new NoResultException("No appointment found");
        }

        checkAuth(appointment.getAppointmentGroup());

        appointmentRepository.delete(appointment);
    }

    public AppointmentDTO setStatus(long id, @Valid AppointmentStatusDTO status) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            throw new NoResultException("No appointment found");
        }

        checkAuth(appointment.getAppointmentGroup());

        appointment.setStatus(status.status());
        appointment.setStatusManuallyOverridden(true);

        return new AppointmentDTO(appointmentRepository.save(appointment));
    }

    public AppointmentStatusDTO getStatus(long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            throw new NoResultException("No appointment found");
        }

        checkAuth(appointment.getAppointmentGroup());

        return new AppointmentStatusDTO(checkStatus(appointment));
    }

    /**
     * Checks if the status of the appointment has changed.
     *
     * @param appointment
     * @return
     */
    public Appointment checkStatus(Appointment appointment) {
        if (appointment.isStatusManuallyOverridden()) {
            return appointment;
        }

        LocalDateTime endTime = appointment.getStartTime().plusMinutes(appointment.getDuration());
        // start time < current time < start time + duration
        if (appointment.getStartTime().isBefore(LocalDateTime.now()) &&
                endTime.isAfter(LocalDateTime.now())) {
            appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        } else if (endTime.isBefore(LocalDateTime.now())) {
                appointment.setStatus(AppointmentStatus.COMPLETED);
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
