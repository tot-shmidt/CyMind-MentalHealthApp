package com.example.myapplication.chat;

public class User {

    private int userId;     // Unique identifier (could be email or numeric ID)
    private String firstName;
    private String lastName;
    private boolean isProfessional; // true if professional, false if student

    public User(int userId, String firstName, String lastName, boolean isProfessional) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isProfessional = isProfessional;
    }

    // --- Getters ---
    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isProfessional() {
        return isProfessional;
    }

    // --- Setters / optional ---
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProfessional(boolean professional) {
        isProfessional = professional;
    }

    // Optional: full name helper
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getRole() {
        if (isProfessional) {
            return "Professional";
        } else {
            return "Student";
        }
    }
}
