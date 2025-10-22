package cymind.service;

import cymind.dto.ProfessionalDTO;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AbstractUserRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ProfessionalService {
    @Autowired
    MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    @Transactional
    public ProfessionalDTO addProfessionalToUser(ProfessionalDTO professionalDTO) throws AuthorizationDeniedException {
        checkAuth(professionalDTO.userId());

        AbstractUser user = abstractUserRepository.findById(professionalDTO.userId().longValue());
        if (user.getUserType() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has registered type");
        }

        MentalHealthProfessional professional = new MentalHealthProfessional(professionalDTO.jobTitle(), professionalDTO.licenseNumber(), user);
        user.setUserType(UserType.PROFESSIONAL);
        abstractUserRepository.save(user);

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
