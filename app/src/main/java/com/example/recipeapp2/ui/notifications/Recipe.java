package com.example.recipeapp2.ui.notifications;

public class Recipe {
    private String id;
    private String name;
    private String description;
    private String allergies;
    private String userId; // Added for data ownership

    public Recipe() {} // Required for Firebase

    public Recipe(String id, String name, String description, String allergies, String userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.allergies = allergies;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}