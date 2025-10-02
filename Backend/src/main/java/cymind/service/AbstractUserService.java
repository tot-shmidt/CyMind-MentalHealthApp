package cymind.service;

import cymind.dto.AbstractUserDTO;
import cymind.dto.CreateAbstractUserDTO;
import cymind.model.AbstractUser;
import cymind.repository.AbstractUserRepository;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AbstractUserService {
    @Autowired
    private AbstractUserRepository abstractUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AbstractUserDTO createUser(CreateAbstractUserDTO createAbstractUserDTO) throws NonUniqueResultException {
        if (abstractUserRepository.findByEmail(createAbstractUserDTO.email()) != null) {
            throw new NonUniqueResultException("Email already in use");
        }

        AbstractUser abstractUser = new AbstractUser(createAbstractUserDTO.firstName(), createAbstractUserDTO.lastName(), createAbstractUserDTO.age(), createAbstractUserDTO.email());

        String hash = passwordEncoder.encode(createAbstractUserDTO.password());
        abstractUser.setPasswordHash(hash);

        return new AbstractUserDTO(abstractUserRepository.save(abstractUser));
    }

    @Transactional
    public void deleteUser(long id) {
        abstractUserRepository.deleteById(id);
    }
}
