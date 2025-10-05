package cymind.service;

import cymind.dto.AbstractUserDTO;
import cymind.dto.CreateAbstractUserDTO;
import cymind.dto.LoginAbstractUserDTO;
import cymind.model.AbstractUser;
import cymind.repository.AbstractUserRepository;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

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
    public AbstractUserDTO getUser(long id) throws AccountNotFoundException, AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != id) {
            throw new AuthorizationDeniedException("Attempting to GET a different user");
        }

        AbstractUser user =  abstractUserRepository.findById(id);

        // If no such user with the given id: HTTP 404 and empty body is sent.
        if (user == null) {
            throw new AccountNotFoundException("Could not find user with that id");
        }

        // HTTP 200 + UserDTO is sent.
        return new AbstractUserDTO(user);
    }

    @Transactional
    public AbstractUser updateUser(long id, AbstractUserDTO request)
            throws AccountNotFoundException, AuthorizationDeniedException, NonUniqueResultException {

        // Check if the currently logged-in user is the one they are trying to update.
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authedUser.getId().equals(id)) {
            throw new AuthorizationDeniedException("You are not authorized to update this user's profile.");
        }

        // Fetch the existing user from the database
        AbstractUser userToUpdate = abstractUserRepository.findById(id);

        // If no such user with the given id: HTTP 400 and empty body is sent.
        if (userToUpdate == null) {
            throw new AccountNotFoundException("User not found with id: " + id);
        }

        // Throw an error if another user is already using that email
        if (abstractUserRepository.findByEmail(request.email()) != null) {
            throw new NonUniqueResultException("Email already in use");
        }

        // Modify the fetched user with data from the request
        userToUpdate.updateFromDTO(request);

        // Save the modified user object
        abstractUserRepository.save(userToUpdate);

        return userToUpdate;
    }

    @Transactional
    public void deleteUser(long id) throws AccountNotFoundException, AuthorizationDeniedException {
        AbstractUser abstractUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (abstractUser.getId() != id) {
            throw new AuthorizationDeniedException("Attempting to delete a different user");
        }

        if  (abstractUserRepository.findById(id) == null) {
            throw new AccountNotFoundException("Could not find user with that id");
        }

        abstractUserRepository.deleteById(id);
    }

    @Transactional
    public AbstractUserDTO loginUser(@Valid LoginAbstractUserDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        return new AbstractUserDTO(abstractUserRepository.findByEmail(request.email()));
    }
}
