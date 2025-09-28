package cymind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import cymind.repository.AbstractUserRepository;

@RestController
@RequestMapping("/user")  // This top level annotation says "Every endpoint inside this controller will start with /users"
public class UserController {
	/**
	 * This let's us an instance of userRepository. RestController does not know about database itself.
	 */
	@Autowired
	AbstractUserRepository userRopository;
	
	// TO-DO: Do I need it?
    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

}
