package cymind.controller;

import cymind.dto.ProfessionalDTO;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AbstractUserRepository;
import cymind.repository.MentalHealthProfessionalRepository;
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
    private MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Autowired
    private AbstractUserRepository abstractUserRepository;

    @PostMapping("/users/professional")
    ResponseEntity<ProfessionalDTO> registerProfessional(@RequestBody @Valid ProfessionalDTO professionalDTO) {
        MentalHealthProfessional mentalHealthProfessional = new MentalHealthProfessional(professionalDTO.jobTitle(), professionalDTO.licenseNumber(),
                abstractUserRepository.findById(professionalDTO.userId()));
        mentalHealthProfessionalRepository.save(mentalHealthProfessional);
        return new ResponseEntity<>(professionalDTO, HttpStatus.CREATED);
    }
}
