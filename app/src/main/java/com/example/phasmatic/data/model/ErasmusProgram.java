package com.example.phasmatic.data.model;

public class ErasmusProgram {
    private String id;
    private String universityId;
    private String name;
    private String websiteUrl;
    private String language;

    public ErasmusProgram(){
    }

    public ErasmusProgram(String id, String universityId, String name, String websiteUrl, String language) {
        this.id = id;
        this.universityId = universityId;
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
