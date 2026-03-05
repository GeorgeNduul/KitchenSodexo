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
}