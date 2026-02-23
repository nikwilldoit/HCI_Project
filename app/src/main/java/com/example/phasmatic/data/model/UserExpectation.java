package com.example.phasmatic.data.model;

public class UserExpectation {
    private String id;
    private String userId;
    private String type;         //erasmus,master,general_advisor
    private String expectations;

    public UserExpectation(){
    }

    public UserExpectation(String id, String userId, String type, String expectations) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.expectations = expectations;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpectations() {
        return expectations;
    }

    public void setExpectations(String expectations) {
        this.expectations = expectations;
    }
}
