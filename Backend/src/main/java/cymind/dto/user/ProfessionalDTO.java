package cymind.dto.user;

import cymind.model.MentalHealthProfessional;
import jakarta.validation.constraints.NotNull;

public record ProfessionalDTO(String jobTitle, String licenseNumber, @NotNull Long userId) {
    public ProfessionalDTO(MentalHealthProfessional professional) {
        this(professional.getJobTitle(), professional.getLicenseNumber(), professional.getAbstractUser().getId());
    }
}
