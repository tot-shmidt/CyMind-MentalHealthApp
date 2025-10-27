package cymind.service;

import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;
import cymind.model.Student;
import cymind.repository.AppointmentGroupRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import cymind.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentGroupService {
    @Autowired
    AppointmentGroupRepository appointmentGroupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    public AppointmentGroup create(AppointmentGroup appointmentGroup) {
        checkAuth(appointmentGroup);

        return appointmentGroupRepository.save(appointmentGroup);
    }

    public List<AppointmentGroup> getByUserPrincipal() {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getUserType() == UserType.STUDENT) {
            Student student = studentRepository.findByAbstractUserId(authedUser.getId());
            return appointmentGroupRepository.findAllByStudent(student);
        } else if (authedUser.getUserType() != UserType.PROFESSIONAL) {
            MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(authedUser.getId());
            return appointmentGroupRepository.findAllByMentalHealthProfessionalsContaining(professional);
        } else {
            throw new AuthorizationDeniedException("Invalid user type");
        }
    }

    public AppointmentGroup getByGroupId(long id) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);
        checkAuth(appointmentGroup);

        return appointmentGroup;
    }

    public AppointmentGroup update(long id, AppointmentGroup appointmentGroup) {
        checkAuth(appointmentGroup);

        AppointmentGroup updatedGroup = appointmentGroupRepository.findById(id);
        updatedGroup.setMentalHealthProfessionals(appointmentGroup.getMentalHealthProfessionals());
        updatedGroup.setStudent(appointmentGroup.getStudent());
        updatedGroup.setGroupName(appointmentGroup.getGroupName());

        return appointmentGroupRepository.save(updatedGroup);
    }

    public void delete(long id) {
        AppointmentGroup appointmentGroup = appointmentGroupRepository.findById(id);
        checkAuth(appointmentGroup);

        // TODO: Delete attached appointments

        appointmentGroupRepository.deleteById(id);
    }

    private void checkAuth(AppointmentGroup appointmentGroup) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getUserType() == UserType.STUDENT) {
            if (authedUser.getId() != appointmentGroup.getStudent().getAbstractUser().getId()) {
                throw new AuthorizationDeniedException("Attempting to access a group without user");
            }
        } else if (authedUser.getUserType() == UserType.PROFESSIONAL) {
            appointmentGroup.getMentalHealthProfessionals().forEach(professional -> {
                if (authedUser.getId() == professional.getAbstractUser().getId()) {
                    return;
                }
            });
            throw new AuthorizationDeniedException("Attempting to access a group without user");
        } else {
            throw new AuthorizationDeniedException("Invalid user type");
        }
    }
}
