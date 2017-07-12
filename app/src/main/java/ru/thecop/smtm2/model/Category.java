package ru.thecop.smtm2.model;

public class Category {
    private Long id;
    private String name;
    private String lowerCaseName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lowerCaseName = name.toLowerCase();
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }
}