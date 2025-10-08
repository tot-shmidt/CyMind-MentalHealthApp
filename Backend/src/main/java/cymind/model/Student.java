package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Student {

	// ========== Fields ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
	 * Major of the Student user.
	 */
	private String major;
	/**
	 * Year of study: freshman, sophomore, etc.
	 */
	private int yearOfStudy;

    @OneToOne()
    @JoinColumn(name = "user_id")
    @NotNull()
    private AbstractUser abstractUser;

    public Student(String major, int yearOfStudy, AbstractUser abstractUser) {
        this.major = major;
        this.yearOfStudy = yearOfStudy;
        this.abstractUser = abstractUser;
    }
}
