package cymind.service;

import cymind.dto.appointment.AppointmentDTO;
import cymind.dto.appointment.AppointmentStatusDTO;
import cymind.enums.AppointmentStatus;
import cymind.enums.UserType;
import cymind.exceptions.AppointmentOverlapException;
import cymind.model.*;
import cymind.repository.AppointmentGroupRepository;
import cymind.repository.AppointmentRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import cymind.dto.appointment.AppointmentNotificationDTO;
import cymind.websocket2.NotificationSocket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentGroupRepository appointmentGroupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Transactional
    public AppointmentDTO create(AppointmentDTO appointmentDTO) throws AppointmentOverlapException {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(appointmentDTO.appointmentGroupId().longValue());
        if (appointmentGroup == null) {
            throw new NoResultException("No appointment group found");
        }

        checkAuth(appointmentGroup);

        // Check for overlapping appointments
        LocalDateTime endTime = appointmentDTO.startTime().plusMinutes(appointmentDTO.duration());
        List<Appointment> overlappingAppointments = appointmentRepository.findAllOverlappingAppointments(appointmentGroup.getMentalHealthProfessionals(), appointmentGroup.getStudent(), appointmentDTO.startTime(), endTime);
        if (!overlappingAppointments.isEmpty()) {
            throw new AppointmentOverlapException(overlappingAppointments, appointmentGroup);
        }

        Appointment appointment = new Appointment(appointmentDTO.startTime(), appointmentDTO.duration(), appointmentGroup);
        appointment.setLocation(appointmentDTO.location());
        appointment.setDescription(appointmentDTO.description());
        appointment.setTitle(appointmentDTO.title());

        // ~~~ MY STUFF FOR NOTIFICATIONS ~~~
        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            Long studentUserId = appointmentGroup.getStudent().getAbstractUser().getId();

            AppointmentNotificationDTO notifDTO = new AppointmentNotificationDTO(
                    "APPOINTMENT_BOOKED",
                    savedAppointment.getTitle(),
                    savedAppointment.getLocation(),
                    savedAppointment.getStartTime()
            );

            NotificationSocket.sendNotificationToUser(studentUserId, notifDTO);

        } catch (Exception e) {
            log.error("Failed to send appointment creation notification for user {}: {}",
                    appointmentGroup.getStudent().getAbstractUser().getId(), e.getMessage());
        }

        return new AppointmentDTO(savedAppointment);
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

    public List<AppointmentDTO> getByUserPrincipal(int num, List<AppointmentStatus> status) {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Appointment> appointmentList;

        if (authedUser.getUserType() == UserType.STUDENT) {
            Student student = studentRepository.findByAbstractUserId(authedUser.getId());
            if (status != null) {
                appointmentList = appointmentRepository.findAllByAppointmentGroup_StudentAndStatusInOrderByStartTimeAsc(student, status);
            } else {
                appointmentList = appointmentRepository.findAllByAppointmentGroup_StudentOrderByStartTimeAsc(student);
            }
        } else if (authedUser.getUserType() == UserType.PROFESSIONAL) {
            MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(authedUser.getId());
            if (status != null) {
                appointmentList = appointmentRepository.findAllByAppointmentGroup_MentalHealthProfessionalsContainingAndStatusInOrderByStartTimeAsc(professional, status);
            } else {
                appointmentList = appointmentRepository.findAllByAppointmentGroup_MentalHealthProfessionalsContainingOrderByStartTimeAsc(professional);
            }
        } else {
            throw new AuthorizationDeniedException("Not a valid user type");
        }

        List<AppointmentDTO> appointmentDTOs = appointmentList.stream().map(AppointmentDTO::new).toList();
        if (num > 0) {
            return appointmentDTOs.subList(0, Math.min(num, appointmentDTOs.size()));
        } else {
            return appointmentDTOs;
        }
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
