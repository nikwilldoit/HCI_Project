package com.example.phasmatic.data.model;

import java.util.List;

public class User_Face_Embedding {

    private String id;
    private String userId;
    User user;
    private List<List<Double>> faceEmbeddings;

    public User_Face_Embedding() {
        // Required empty constructor for Firebase
    }

    public User_Face_Embedding(String id, String userId, List<List<Double>> faceEmbeddings, User user) {
        this.id = id;
        this.userId = userId;
        this.faceEmbeddings = faceEmbeddings;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<List<Double>> getFaceEmbeddings() {
        return faceEmbeddings;
    }

    public void setFaceEmbeddings(List<List<Double>> faceEmbeddings) {
        this.faceEmbeddings = faceEmbeddings;
    }
}