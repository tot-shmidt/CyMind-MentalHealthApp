package cymind.controller;

import cymind.dto.AbstractUserDTO;
import cymind.dto.CreateAbstractUserDTO;
import cymind.dto.LoginAbstractUserDTO;
import cymind.model.AbstractUser;
import cymind.repository.AbstractUserRepository;
import cymind.service.AbstractUserService;
import jakarta.persistence.NonUniqueResultException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
public class UserController {
    /**
     * This lets us an instance of userRepository. RestController does not know about database itself.
     */
    @Autowired
    AbstractUserRepository userRepository;

    // Use Service Middleware to interact with User table
	@Autowired
    AbstractUserService abstractUserService;

    /**
     * Returns List of all users. It responds on "GET /users", as in RequestMapping annotation.
     * @return
     */
    @GetMapping("/users")
    List<AbstractUser> getAllUsers() {
    	return userRepository.findAll();
    }

    /**
     * Returns user by its id.
     * @param id
     * @return
     */
    @GetMapping(path = "/users/{id}")
    ResponseEntity<AbstractUserDTO> getUserById(@PathVariable long id) throws AccountNotFoundException, AuthorizationDeniedException {
        AbstractUserDTO userDTO = abstractUserService.getUser(id);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * Updates user`s data with specified id.
     * @param id
     * @param request
     * @return
     */
    @PutMapping(path = "/users/{id}")
    ResponseEntity<AbstractUserDTO> updateUser(@PathVariable long id, @RequestBody AbstractUserDTO request)
            throws AccountNotFoundException, AuthorizationDeniedException, NonUniqueResultException {

        // Send all the work to the service class.
        AbstractUser updatedUser = abstractUserService.updateUser(id, request);

        // Convert the updated user entity to a DTO and return it.
        return ResponseEntity.ok(new AbstractUserDTO(updatedUser));
    }

    /**
     * Deletes the user with {id}, currently does not check if the user requesting that deletion is that user
     * @param id
     * @return HTTP 200
     */
    @DeleteMapping("/users/{id}")
    ResponseEntity<?> deleteUser(@PathVariable long id) throws AccountNotFoundException, AuthorizationDeniedException {
        abstractUserService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Create a new user under /signup. Will be validated for a correct email and password
     * @param request
     * @return The created user
     */
    @PostMapping("/signup")
    ResponseEntity<AbstractUserDTO> register(@RequestBody @Valid CreateAbstractUserDTO request) throws NonUniqueResultException {
        AbstractUserDTO newUser = abstractUserService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<AbstractUserDTO> login(@RequestBody @Valid LoginAbstractUserDTO request) {
        AbstractUserDTO newUser = abstractUserService.loginUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
}
