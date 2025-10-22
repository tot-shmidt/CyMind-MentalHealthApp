package cymind.service;

import cymind.dto.ProfessionalDTO;
import cymind.model.AbstractUser;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AbstractUserRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfessionalService {
    @Autowired
    MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    public ProfessionalDTO addProfessionalToUser(ProfessionalDTO professionalDTO) throws AuthorizationDeniedException {
        checkAuth(professionalDTO.userId());

        MentalHealthProfessional professional = new MentalHealthProfessional(professionalDTO.jobTitle(), professionalDTO.licenseNumber(),
                abstractUserRepository.findById(professionalDTO.userId()));
        return new ProfessionalDTO(mentalHealthProfessionalRepository.save(professional));
    }

    private void checkAuth(long id) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != id) {
            log.warn("User {} attempted to modify user type for User {}", authedUser.getId(), id);
            throw new AuthorizationDeniedException("Attempting to modify a different user");
        }
    }
}
