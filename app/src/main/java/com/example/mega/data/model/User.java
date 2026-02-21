package com.example.mega.data.model;

public class User {

    private int id;
    private String fullname;
    private String emailAddress;
    private String password;
    private String dateOfBirth;
    private String phoneNumber;
    private String bio;

    public User(int id, String fullname, String emailAddress, String password,
                String dateOfBirth, String phoneNumber, String bio) {
        this.id = id;
        this.fullname = fullname;
        this.emailAddress = emailAddress;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
