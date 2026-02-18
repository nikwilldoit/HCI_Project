package com.example.phasmatic;

public class Users {

    private int id;
    private String fullname;
    private String emailAddress;
    private String password;
    private String date_of_birth;
    private String phone_number;
    private String bio;

    public Users(int id, String fullname, String emailAddress, String password, String date_of_birth, String phone_number, String bio) {
        this.id = id;
        this.fullname = fullname;
        this.emailAddress = emailAddress;
        this.password = password;
        this.date_of_birth = date_of_birth;
        this.phone_number = phone_number;
        this.bio = bio;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
