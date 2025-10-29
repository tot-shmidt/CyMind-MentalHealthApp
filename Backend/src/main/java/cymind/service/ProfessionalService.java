package cymind.service;

import cymind.dto.user.ProfessionalDTO;
import cymind.dto.user.ProfessionalPublicDTO;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AbstractUserRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProfessionalService {
    @Autowired
    MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    /**
     * Register the User as a Mental Health Professional
     *
     * @param professionalDTO
     * @return
     * @throws AuthorizationDeniedException
     */
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

    @Transactional
    public List<ProfessionalPublicDTO> getAll(int num) {
        List<ProfessionalPublicDTO> professionals = mentalHealthProfessionalRepository.findAll().stream()
                .map(ProfessionalPublicDTO::new)
                .toList();

        if (num > 0) {
            return professionals.subList(0, Math.min(num, professionals.size()));
        } else {
            return professionals;
        }
    }

    @Transactional
    public List<ProfessionalPublicDTO> getAll(String name, int num) {
        List<MentalHealthProfessional> professionals;
        String[] nameParts = name.split(" ");
        if (nameParts.length > 1) {
            String firstName = nameParts[0];
            String lastName = nameParts[1];
            professionals = mentalHealthProfessionalRepository.findByAbstractUser_FirstNameContainingAndAbstractUser_LastNameContainingOrderByAbstractUser(firstName, lastName);
        } else {
            professionals = mentalHealthProfessionalRepository.findByName(name);
        }

        List<ProfessionalPublicDTO> professionalPublicDTOs = professionals.stream()
                .map(ProfessionalPublicDTO::new)
                .toList();
        if (num > 0) {
            return professionalPublicDTOs.subList(0, Math.min(num, professionals.size()));
        } else {
            return professionalPublicDTOs;
        }
    }

    @Transactional
    public Record get(long id) {
        MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(id);
        if (professional == null) {
            throw new NoResultException("Professional not found");
        }

        try {
            checkAuth(id);
            return new ProfessionalDTO(professional);
        } catch (AuthorizationDeniedException e) {
            return new ProfessionalPublicDTO(professional);
        }
    }

    @Transactional
    public ProfessionalDTO update(long id, ProfessionalDTO professionalDTO) {
        MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(id);
        if (professional == null) {
            throw new NoResultException("Professional not found");
        }

        checkAuth(professionalDTO.userId());

        professional.setJobTitle(professionalDTO.jobTitle());
        professional.setLicenseNumber(professionalDTO.licenseNumber());

        log.info("Updated professional {}", professional.getId());
        return new ProfessionalDTO(mentalHealthProfessionalRepository.save(professional));
    }

    @Transactional
    public void remove(long id) throws AuthorizationDeniedException {
        checkAuth(id);

        MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(id);
        if (professional == null) {
            throw new NoResultException("Professional not found");
        }

        AbstractUser abstractUser = abstractUserRepository.findById(id);
        if (abstractUser == null) {
            throw new NoResultException("User not found");
        }
        abstractUser.setUserType(null);
        abstractUserRepository.save(abstractUser);

        mentalHealthProfessionalRepository.deleteById(professional.getId());
        log.info("Deleted professional {} (user {})", professional.getId(), professional.getAbstractUser().getId());
    }

    private void checkAuth(long id) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != id) {
            log.warn("User {} attempted to modify user type for User {}", authedUser.getId(), id);
            throw new AuthorizationDeniedException("Attempting to modify a different user");
        }
    }
}
