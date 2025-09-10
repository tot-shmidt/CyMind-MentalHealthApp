package coms309;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello this is COMS 3090\n";
    }

    @PostMapping("/user/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        UserManager.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Welcome to 3090: " + user.name());
    }

    @GetMapping("/user/{name}")
    public User getUser(@PathVariable String name) {
        User user = UserManager.getUser(name);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        return user;
    }
}
