package coms309;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 3090\n";
    }

    @GetMapping("/welcome/{name}")
    public String welcome(@PathVariable String name) {
        UserManager.addUser(new User(name));
        return "Welcome to COMS 3090: " + name + "\n";
    }

    @GetMapping("/user/{name}")
    public String getUser(@PathVariable String name) {
        User user = UserManager.getUser(name);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        return user.name() + "\n";
    }
}
