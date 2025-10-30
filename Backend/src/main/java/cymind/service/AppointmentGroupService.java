package cymind.service;

import cymind.dto.appointment.AppointmentDTO;
import cymind.dto.appointment.AppointmentGroupDTO;
import cymind.dto.appointment.CreateAppointmentGroupDTO;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;
import cymind.model.Student;
import cymind.repository.AppointmentGroupRepository;
import cymind.repository.AppointmentRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AppointmentGroupService {
    @Autowired
    AppointmentGroupRepository appointmentGroupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public AppointmentGroupDTO create(CreateAppointmentGroupDTO appointmentGroupDTO) {
        Student student = studentRepository.findByAbstractUserId(appointmentGroupDTO.studentId());
        if (student == null) {
            throw new NoResultException("Specified student user is not a student");
        }

        // List of ids -> List of MentalHealthProfessionals
        List<MentalHealthProfessional> professionalList = mentalHealthProfessionalRepository.findAllByAbstractUserIdIn(appointmentGroupDTO.professionalIds());
        if (professionalList.isEmpty()) {
            throw new NoResultException("No specified professional users are professionals");
        }

        AppointmentGroup appointmentGroup = new AppointmentGroup(professionalList, student, appointmentGroupDTO.groupName());

        checkAuth(appointmentGroup);

        return new AppointmentGroupDTO(appointmentGroupRepository.save(appointmentGroup));
    }

    @Transactional
    public List<AppointmentGroupDTO> getByUserPrincipal() {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getUserType() == UserType.STUDENT) {
            Student student = studentRepository.findByAbstractUserId(authedUser.getId());
            return appointmentGroupRepository.findAllByStudent(student).stream()
                    .map(AppointmentGroupDTO::new)
                    .toList();
        } else if (authedUser.getUserType() == UserType.PROFESSIONAL) {
            MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(authedUser.getId());
            return appointmentGroupRepository.findAllByMentalHealthProfessionalsContaining(professional).stream()
                    .map(AppointmentGroupDTO::new)
                    .toList();
        } else {
            throw new AuthorizationDeniedException("Invalid user type");
        }
    }

    @Transactional
    public AppointmentGroupDTO getByGroupId(long id) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);
        checkAuth(appointmentGroup);

        return new AppointmentGroupDTO(appointmentGroup);
    }

    @Transactional
    public List<AppointmentDTO> getAppointmentByGroupId(long id, int num) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);
        checkAuth(appointmentGroup);

        List<AppointmentDTO> appointments = appointmentRepository.findByAppointmentGroupOrderByStartTimeDesc(appointmentGroup).stream()
                .map(AppointmentDTO::new)
                .toList();

        if (num > 0) {
            return appointments.subList(0, Math.min(num, appointments.size()));
        } else {
            return appointments;
        }
    }

    @Transactional
    public AppointmentGroupDTO update(long id, CreateAppointmentGroupDTO appointmentGroupDTO) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);

        checkAuth(appointmentGroup);

        // List of ids -> List of MentalHealthProfessionals
        List<MentalHealthProfessional> professionalList = mentalHealthProfessionalRepository.findAllByAbstractUserIdIn(appointmentGroupDTO.professionalIds());
        if (professionalList.isEmpty()) {
            throw new NoResultException("No specified professional users are professionals");
        }

        Student student = studentRepository.findByAbstractUserId(appointmentGroupDTO.studentId());
        if (student == null) {
            throw new NoResultException("Specified student user is not a student");
        }

        appointmentGroup.setMentalHealthProfessionals(professionalList);
        appointmentGroup.setStudent(student);
        appointmentGroup.setGroupName(appointmentGroupDTO.groupName());

        AppointmentGroup res = appointmentGroupRepository.save(appointmentGroup);
        return new AppointmentGroupDTO(res);
    }

    @Transactional
    public void delete(long id) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);
        checkAuth(appointmentGroup);

        // TODO: Delete attached appointments

        appointmentGroupRepository.deleteById(id);
    }

    private void checkAuth(AppointmentGroup appointmentGroup) throws AuthorizationDeniedException {
        if (appointmentGroup == null) {
            throw new AuthorizationDeniedException("Invalid result");
        }

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
