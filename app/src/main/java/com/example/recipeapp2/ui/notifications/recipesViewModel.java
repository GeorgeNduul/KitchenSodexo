package com.example.recipeapp2.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class recipesViewModel extends ViewModel {

    private final MutableLiveData<List<Recipe>> recipeList;
    private final FirebaseFirestore db;

    public recipesViewModel() {
        db = FirebaseFirestore.getInstance();
        recipeList = new MutableLiveData<>(new ArrayList<>());
        loadRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipeList;
    }

    private void loadRecipes() {
        // Real-time listener: will trigger every time data changes in Firebase
        db.collection("recipes").addSnapshotListener((value, error) -> {
            if (error != null) return;

            List<Recipe> recipes = new ArrayList<>();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {
                    Recipe recipe = doc.toObject(Recipe.class);
                    recipes.add(recipe);
                }
                recipeList.setValue(recipes);
            }
        });
    }

    // Inside recipesViewModel.java
    public void addRecipe(String name, String desc, String allergies) {
        String id = db.collection("recipes").document().getId();
        // Pass the allergies string into the constructor
        Recipe newRecipe = new Recipe(id, name, desc, allergies);
        db.collection("recipes").document(id).set(newRecipe);
    }
}