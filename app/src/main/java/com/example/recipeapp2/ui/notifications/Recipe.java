package com.example.recipeapp2.ui.notifications;

public class Recipe {
    private String id;
    private String name;
    private String description;
    private String allergies;

    // Required empty constructor for Firebase
    public Recipe() {}

    public Recipe(String id, String name, String description, String allergies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.allergies = allergies;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAllergies() { return allergies; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
}