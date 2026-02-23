package com.example.phasmatic.data.model;

public class University {
    private String id;
    private String name;
    private String city;
    private String country;
    private Integer ranking;
    private String websiteUrl;

    public University(){
    }

    public University(String id, String name, String city, String country, Integer ranking, String websiteUrl) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.ranking = ranking;
        this.websiteUrl = websiteUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
