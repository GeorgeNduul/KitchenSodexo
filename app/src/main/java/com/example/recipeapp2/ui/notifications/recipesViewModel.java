package com.example.recipeapp2.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class recipesViewModel extends ViewModel {

    private final MutableLiveData<List<Recipe>> recipeList = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<List<Recipe>> getRecipes() {
        return recipeList;
    }

    public void fetchRecipes() {
        String uid = auth.getUid();
        if (uid == null) return;

        // Listen to Firestore in real-time
        db.collection("recipes")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        List<Recipe> updatedList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Recipe r = doc.toObject(Recipe.class);
                            r.setId(doc.getId());
                            updatedList.add(r);
                        }
                        // Update the LiveData broadcast
                        recipeList.setValue(updatedList);
                    }
                });
    }
}