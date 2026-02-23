package com.example.phasmatic.data.model;

public class MasterProgram {
    private String id;
    private String universityId;
    private String name;
    private Integer ranking;
    private String description;
    private String websiteUrl;
    private String language;

    public MasterProgram(){
    }

    public MasterProgram(String id, String universityId, String name, Integer ranking, String description, String websiteUrl, String language) {
        this.id = id;
        this.universityId = universityId;
        this.name = name;
        this.ranking = ranking;
        this.description = description;
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

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
