package com.example.recipeapp2.ui.notifications;

public class Recipe {
    private String id;
    private String name;
    private String description;
    private String allergies;

    public Recipe() {} // Required for Firebase

    public Recipe(String id) {
        this.id = id;
        this.name = "Test";
        this.description = "Another test";
        this.allergies = "Something else";
    }

    public Recipe(String id, String name, String description, String allergies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.allergies = allergies;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
}