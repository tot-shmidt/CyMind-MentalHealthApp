package com.example.myapplication.chat;

public class Professional {
    private int professionalId;
    private String name;
    private String specialization;
    private boolean isSelected;

    public Professional(int professionalId, String name, String specialization) {
        this.professionalId = professionalId;
        this.name = name;
        this.specialization = specialization;
        this.isSelected = false;
    }

    public int getProfessionalId() { return professionalId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }
}