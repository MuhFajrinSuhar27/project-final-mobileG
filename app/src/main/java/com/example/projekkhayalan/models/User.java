package com.example.projekkhayalan.models;

public class User {
    private String id;
    private String name;
    private String phone;
    private String address;
    private int disabilityType;
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Constructor
    public User(String id, String name, String phone, int disabilityType) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.disabilityType = disabilityType;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDisabilityType() {
        return disabilityType;
    }

    public void setDisabilityType(int disabilityType) {
        this.disabilityType = disabilityType;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
}