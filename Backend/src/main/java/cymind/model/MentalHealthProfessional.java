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
public class MentalHealthProfessional extends AbstractUser {
	
	// ========== Fields ==========
	
	/**
	 * What occupation does the person have.
	 */
	private String jobTitle;
	/**
	 * TO-DO: Do we need this?
	 */
	private String licenseNumber;
	
	
	// ========== Constructors ==========
	/**
	 * Creates an object of a MentalHealthProfessional user. First envokes parent AbstractUser class.
	 * @param firstName
	 * @param lastName
	 * @param ageFullYears
	 * @param emailId
	 * @param jobTitle
	 * @param licenseNumber
	 */
	public MentalHealthProfessional(String firstName, String lastName, Integer ageFullYears, String emailId, String jobTitle, String licenseNumber) {
        super(firstName, lastName, ageFullYears, emailId);
        this.jobTitle = jobTitle;
        this.licenseNumber = licenseNumber;
    }

	/**
     * Default constuctor is required by JPA/Spring to recreate objects from the data base.
     */
	public MentalHealthProfessional() {}
	
	
	// ========== Getters and Setters ==========

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}
	
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
}
