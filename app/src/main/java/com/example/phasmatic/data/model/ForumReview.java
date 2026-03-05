package com.example.phasmatic.data.model;

public class ForumReview {
    public String id;
    public String userId;
    public String userName;
    public String type;      //erasmus,master
    public String university;
    public String country;
    public String text;
    public float rating;     //1-5
    public long timestamp;

    public ForumReview() {
    }

    public ForumReview(String id, String userId, String userName, String type, String university, String country, String text, float rating, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.university = university;
        this.country = country;
        this.text = text;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
