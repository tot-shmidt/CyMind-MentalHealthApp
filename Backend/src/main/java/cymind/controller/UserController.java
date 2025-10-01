package cymind.controller;

import java.util.List;

import cymind.dto.AbstractUserDTO;
import cymind.service.AbstractUserService;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cymind.repository.AbstractUserRepository;
import cymind.model.*;

@RestController
@RequestMapping("/users")  // This top level annotation says "Every endpoint inside this controller will start with /users"
public class UserController {
    /**
     * This lets us an instance of userRepository. RestController does not know about database itself.
     */
    @Autowired
    AbstractUserRepository userRepository;

    // Use Service Middleware to interact with User table
	@Autowired
    AbstractUserService abstractUserService;

	// TO-DO: Do I need it?
    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    
    /**
     * Returns List of all users. It responds on "GET /users", as in RequestMapping annotation.
     * @return
     */
    @GetMapping
    List<AbstractUser> getAllUsers() {
    	return userRepository.findAll();
    }
    
    /**
     * Returns user by its id.
     * @param id
     * @return
     */
    @GetMapping(path = "/{id}")
    ResponseEntity<AbstractUser> getUserById(@PathVariable long id) {
    	AbstractUser user =  userRepository.findById(id);
    	
    	// If no such user with the given id: HTTP 404 and empty body is sent.
    	if (user == null) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	// HTTP 200 + user JSON is sent.
    	return ResponseEntity.ok(user);
    }
    
    /**
     * Updates user`s data with specified id.
     * @param id
     * @param request
     * @return
     */
    @PutMapping(path = "/{id}")
    ResponseEntity<AbstractUser> updateUser(@PathVariable long id, @RequestBody AbstractUser request) {
    	AbstractUser user = userRepository.findById(id);
    	
    	// If no such user with the given id: HTTP 404 and empty body is sent.
    	if (user == null) {
    		return ResponseEntity.notFound().build();
    	}
    	
    	// Check if id from http body is the same with one of the current user.
    	if (request.getId() != null && !request.getId().equals(id)) {
    		throw new RuntimeException("Path variable id is different from request body id");
    	}
    	
    	userRepository.save(request);
    	return ResponseEntity.ok(user);  	
    }

    /**
     * Create a new user under /user/signup. Will be validated for a correct email and password
     * @param request
     * @return The created user
     */
    @PostMapping("/signup")
    ResponseEntity<AbstractUserDTO> createUser(@RequestBody AbstractUser request) throws NonUniqueResultException {
        AbstractUserDTO newUser = abstractUserService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @ExceptionHandler(NonUniqueResultException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleNonUniqueResultException(NonUniqueResultException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
