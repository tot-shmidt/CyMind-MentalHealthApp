package cymind.model;

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
public class Guest extends AbstractUser{
	
	// ========== Fields ==========
	
	/**
	 * Unique identifier of a guest user.
	 */
	private String sessionToken;
	/**
	 * Integer represents the reason for visit depending on selected option from provided list of options. 
	 */
	private int reasonForVisit;
	
	
	// ========== Constructors ==========

	/**
	 * Creates an object of a guest user. First envokes parent AbstractUser class. F
	 * @param firstName
	 * @param lastName
	 * @param ageFullYears
	 * @param emailId
	 * @param sessionToken
	 */
	public Guest(String firstName, String lastName, Integer ageFullYears, String emailId, String sessionToken, int reasonForVisit) {
        super(firstName, lastName, ageFullYears, emailId);
        this.sessionToken = sessionToken;
        this.setReasonForVisit(reasonForVisit);
    }
	
	/**
     * Default constuctor is required by JPA/Spring to recreate objects from the data base.
     */
	public Guest() {}
	
	
	// ========== Getters and Setters =========
	
	public String getSessionToken() {
		return sessionToken;
	}


	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public int getReasonForVisit() {
		return reasonForVisit;
	}

	public void setReasonForVisit(int reasonForVisit) {
		this.reasonForVisit = reasonForVisit;
	}
}
