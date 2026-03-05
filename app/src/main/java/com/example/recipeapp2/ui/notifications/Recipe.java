package com.example.recipeapp2.ui.notifications;

public class Recipe {
    private String id;
    private String name;
    private String description;

    // Required empty constructor for Firebase
    public Recipe() {}

    public Recipe(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters - These fix the "Cannot resolve method getName" errors
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    // Setters - Required for Firebase to fill the object with data
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    // Inside Recipe.java
    private String allergies;

    // Update your constructor
    public Recipe(String id, String name, String description, String allergies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.allergies = allergies;
    }

    // Add the Getter and Setter
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
}