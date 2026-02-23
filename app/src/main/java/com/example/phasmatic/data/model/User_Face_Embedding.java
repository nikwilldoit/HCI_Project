package com.example.phasmatic.data.model;

import java.util.List;

public class User_Face_Embedding {
    private String id;
    private String userId;
    private List<Double> faceEmbedding;

    User user;

    public User_Face_Embedding(){
    }

    public User_Face_Embedding(String id, String userId, List<Double> faceEmbedding, User user) {
        this.id = id;
        this.userId = userId;
        this.faceEmbedding = faceEmbedding;
        this.user = user;
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

    public List<Double> getFaceEmbedding() {
        return faceEmbedding;
    }

    public void setFaceEmbedding(List<Double> faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }
}
