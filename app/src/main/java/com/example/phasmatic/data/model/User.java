package com.example.phasmatic.data.model;

public class User {

    private String id;
    private String fullName;
    private String email;
    private String password;
    private String dateOfBirth;
    private String phoneNumber;
    private String bio;

    public User() {
    }

    public User(String id, String fullName, String email, String password,
                String dateOfBirth, String phoneNumber, String bio) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBio() { return bio; }

    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBio(String bio) { this.bio = bio; }
}
