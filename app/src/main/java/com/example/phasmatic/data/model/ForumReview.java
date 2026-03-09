package com.example.phasmatic.data.model;

public class ForumReview {
    public String id;
    public String user_id;
    public String user_name;
    public String type; //erasmus,master
    public String university;
    public String country;
    public String text;
    public float rating; //1-5
    public int likes = 0;
    public String timestamp;
    public int comments=0;
    public int views;



    public ForumReview() {}

    public ForumReview(String id,
                       String user_id,
                       String user_name,
                       String type,
                       String university,
                       String country,
                       String text,
                       float rating,
                       long likes,
                       String timestamp,
                       int views) {

        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.type = type;
        this.university = university;
        this.country = country;
        this.text = text;
        this.rating = rating;
        this.likes = Math.toIntExact(likes);
        this.timestamp = timestamp;
        this.views = views;
    }
    public int getViews(){
        return this.views;
    }
    public void setViews(int v){
        this.views = v;
    }
    public void addView(){
        this.views++;
    }
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

