package cymind.dto.user;

import cymind.model.MentalHealthProfessional;

public record ProfessionalPublicDTO(Long userId, String firstName, String lastName, String jobTitle) {
    public ProfessionalPublicDTO(MentalHealthProfessional professional) {
        this(
                professional.getAbstractUser().getId(),
                professional.getAbstractUser().getFirstName(),
                professional.getAbstractUser().getLastName(),
                professional.getJobTitle()
        );
    }
}
