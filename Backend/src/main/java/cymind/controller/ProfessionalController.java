package cymind.controller;

import cymind.dto.ProfessionalDTO;
import cymind.service.ProfessionalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfessionalController {
    @Autowired
    ProfessionalService professionalService;

    @PostMapping("/users/professional")
    ResponseEntity<ProfessionalDTO> registerProfessional(@RequestBody @Valid ProfessionalDTO professionalDTO) {
        return new ResponseEntity<>(professionalService.addProfessionalToUser(professionalDTO), HttpStatus.CREATED);
    }
}
