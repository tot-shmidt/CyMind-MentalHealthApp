package cymind.service;

import cymind.dto.user.AbstractUserDTO;
import cymind.dto.user.CreateAbstractUserDTO;
import cymind.dto.user.LoginAbstractUserDTO;
import cymind.model.AbstractUser;
import cymind.model.Student;
import cymind.repository.AbstractUserRepository;
import cymind.repository.MoodEntryRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
@Slf4j
public class AbstractUserService {
    @Autowired
    private AbstractUserRepository abstractUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Transactional
    public AbstractUserDTO createUser(CreateAbstractUserDTO createAbstractUserDTO) throws NonUniqueResultException {
        if (abstractUserRepository.findByEmail(createAbstractUserDTO.email()) != null) {
            throw new NonUniqueResultException("Email already in use");
        }

        AbstractUser abstractUser = new AbstractUser(createAbstractUserDTO.firstName(), createAbstractUserDTO.lastName(), createAbstractUserDTO.age(), createAbstractUserDTO.email());

        String hash = passwordEncoder.encode(createAbstractUserDTO.password());
        abstractUser.setPasswordHash(hash);

        abstractUser = abstractUserRepository.save(abstractUser);
        log.info("Created User with id: {}", abstractUser.getId());
        return new AbstractUserDTO(abstractUser);
    }

    @Transactional
    public AbstractUserDTO getUser(long id) throws AccountNotFoundException, AuthorizationDeniedException {
        checkAuth(id);

        AbstractUser user =  abstractUserRepository.findById(id);
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
        checkAuth(id);

        // Fetch the existing user from the database
        AbstractUser userToUpdate = abstractUserRepository.findById(id);

        // If no such user with the given id: HTTP 400 and empty body is sent.
        if (userToUpdate == null) {
            throw new AccountNotFoundException("User not found with id: " + id);
        }

        // Throw an error if another user is already using that email
        if (!request.email().equals(userToUpdate.getEmail()) && abstractUserRepository.findByEmail(request.email()) != null) {
            throw new NonUniqueResultException("Email already in use");
        }

        // Modify the fetched user with data from the request
        userToUpdate.updateFromDTO(request);

        // Save the modified user object
        abstractUserRepository.save(userToUpdate);

        log.info("Updated User with id: {}", userToUpdate.getId());
        return userToUpdate;
    }

    @Transactional
    public void deleteUser(long id) throws AccountNotFoundException, AuthorizationDeniedException {
        checkAuth(id);

        if  (abstractUserRepository.findById(id) == null) {
            throw new AccountNotFoundException("Could not find user with that id");
        }

        Student student = studentRepository.findByAbstractUserId(id);
        if (student != null) {
            moodEntryRepository.deleteAllByStudent(student);
            studentRepository.deleteById(student.getId());
        }

        log.info("Deleted User with id: {}", id);
        abstractUserRepository.deleteById(id);
    }

    @Transactional
    public AbstractUserDTO loginUser(@Valid LoginAbstractUserDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        log.debug("Authenticated User with email: {}", request.email());
        return new AbstractUserDTO(abstractUserRepository.findByEmail(request.email()));
    }

    private void checkAuth(long id) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != id) {
            log.warn("User {} attempted to access information for User {}", authedUser.getId(), id);
            throw new AuthorizationDeniedException("Attempting to access a different user");
        }
    }
}
