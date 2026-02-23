package com.example.phasmatic.data.model;

public class ErasmusCourse {
    private String id;
    private String erasmusId;
    private String name;
    private Double ects;
    private String teacher;
    private String description;

    public ErasmusCourse(){
    }
    public ErasmusCourse(String id, String erasmusId, String name, Double ects, String teacher, String description) {
        this.id = id;
        this.erasmusId = erasmusId;
        this.name = name;
        this.ects = ects;
        this.teacher = teacher;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getErasmusId() {
        return erasmusId;
    }

    public void setErasmusId(String erasmusId) {
        this.erasmusId = erasmusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getEcts() {
        return ects;
    }

    public void setEcts(Double ects) {
        this.ects = ects;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
