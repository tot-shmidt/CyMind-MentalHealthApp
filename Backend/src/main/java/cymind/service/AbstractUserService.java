package cymind.service;

import cymind.dto.AbstractUserDTO;
import cymind.model.AbstractUser;
import cymind.repository.AbstractUserRepository;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbstractUserService {
    @Autowired
    private AbstractUserRepository abstractUserRepository;

    @Transactional
    public AbstractUserDTO createUser(AbstractUser abstractUser) throws NonUniqueResultException {
        if (abstractUserRepository.findByEmail(abstractUser.getEmail()) != null) {
            throw new NonUniqueResultException("Email already in use");
        }

        return new AbstractUserDTO(abstractUserRepository.save(abstractUser));
    }
}
