package myUsers;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Student extends AbstractUser {
	
	// ========== Fields ==========
	/**
	 * Major of the Student user.
	 */
	private String major;
	/**
	 * Year of study: freshman, sophomore, etc.
	 */
	private String yearOfStudy;
	
	
	// ========== Constructors ==========
	
	/**
	 * Creates an object of a Student user. First envokes parent AbstractUser class.
	 * @param firstName
	 * @param lastName
	 * @param ageFullYears
	 * @param emailId
	 * @param major
	 * @param yearOfStudy
	 */
	public Student(String firstName, String lastName, Integer ageFullYears, String emailId, String major, String yearOfStudy) {
        super(firstName, lastName, ageFullYears, emailId);
        this.major = major;
        this.yearOfStudy = yearOfStudy;
    }
	
	/**
     * Default constuctor is required by JPA/Spring to recreate objects from the data base.
     */
	public Student() {}
	
	
	// ========== Getters and Setters ==========
	
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGraduationYear() {
        return yearOfStudy;
    }

    public void setGraduationYear(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

}
