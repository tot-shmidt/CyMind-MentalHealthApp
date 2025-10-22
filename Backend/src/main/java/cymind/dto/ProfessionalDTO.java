package cymind.dto;

import cymind.model.MentalHealthProfessional;

public record ProfessionalDTO(String jobTitle, String licenseNumber, long userId) {
    public ProfessionalDTO(MentalHealthProfessional professional) {
        this(professional.getJobTitle(), professional.getLicenseNumber(), professional.getAbstractUser().getId());
    }
}
