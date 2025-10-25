package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MentalHealthProfessional {

    // ========== Fields ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * What occupation does the person have.
     */
    private String jobTitle;

    /**
     * The license number of the register professional
     */
    private String licenseNumber;

    @OneToOne()
    @JoinColumn(name = "user_id")
    @NotNull()
    private AbstractUser abstractUser;

    // ========== Constructors ==========

    /**
     * Creates an object of a MentalHealthProfessional user. First envokes parent AbstractUser class.
     *
     * @param jobTitle
     * @param licenseNumber
     * @param abstractUser
     */
    public MentalHealthProfessional(String jobTitle, String licenseNumber, AbstractUser abstractUser) {
        this.jobTitle = jobTitle;
        this.licenseNumber = licenseNumber;
        this.abstractUser = abstractUser;
    }
}
