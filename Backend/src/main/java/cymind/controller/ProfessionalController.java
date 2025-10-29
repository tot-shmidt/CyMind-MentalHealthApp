package cymind.controller;

import cymind.dto.user.ProfessionalDTO;
import cymind.dto.user.PublicUserDTO;
import cymind.service.ProfessionalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfessionalController {
    @Autowired
    ProfessionalService professionalService;

    @PostMapping("/users/professional")
    ResponseEntity<ProfessionalDTO> registerProfessional(@RequestBody @Valid ProfessionalDTO professionalDTO) {
        return new ResponseEntity<>(professionalService.addProfessionalToUser(professionalDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users/professional")
    ResponseEntity<List<PublicUserDTO>> getAllProfessionals(@RequestParam(required = false) String name, @RequestParam(required = false) Integer num) {
        if (num == null) {
            num = -1;
        }

        if (name != null && !name.isBlank()) {
            return new ResponseEntity<>(professionalService.getAll(name, num), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(professionalService.getAll(num), HttpStatus.OK);
        }
    }

    @GetMapping("/users/professional/{id}")
    ResponseEntity<?> getProfessional(@PathVariable long id) {
        return new ResponseEntity<>(professionalService.get(id), HttpStatus.OK);
    }

    @PutMapping("/users/professional/{id}")
    ResponseEntity<ProfessionalDTO> updateProfessional(@PathVariable long id, @RequestBody ProfessionalDTO professionalDTO) {
        return new ResponseEntity<>(professionalService.update(id, professionalDTO), HttpStatus.OK);
    }

    @DeleteMapping("/users/professional/{id}")
    ResponseEntity<?> deleteProfessional(@PathVariable long id) {
        professionalService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
