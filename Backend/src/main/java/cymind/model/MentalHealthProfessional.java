package cymind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy = "authors")
    @JsonIgnore
    private Set<Article> articles = new HashSet<>();


    // ========== Constructors ==========

    /**
     * Creates an object of a MentalHealthProfessional user. First invokes parent AbstractUser class.
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
