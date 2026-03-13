package com.example.recipeapp2.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class recipesFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private List<Recipe> recipeList_v2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private FloatingActionButton fab;
    private TextView tvSearchStatus; // Added to show search context

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        recyclerView = view.findViewById(R.id.recipeRecyclerView);
        fab = view.findViewById(R.id.fabAddRecipe);

        // Use a simple TextView for search status (Optional: Add this to your fragment_recipe.xml)
        // For now, we will handle logic; ensure your RecyclerView is correctly ID'd.

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RecipeAdapter(recipeList_v2, new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                // Future: Show details
            }

            @Override
            public void onDeleteClick(Recipe recipe) {
                showDeleteConfirmation(recipe);
            }
        });

        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(v -> showAddRecipeDialog());

        loadRecipesFromFirestore();

        return view;
    }

    private void loadRecipesFromFirestore() {
        if (mAuth.getCurrentUser() == null) return;

        String currentUid = mAuth.getCurrentUser().getUid();

        // 1. Capture the search query sent from HomeFragment
        final String searchQuery = getArguments() != null ? getArguments().getString("search_query") : null;

        db.collection("recipes")
                .whereEqualTo("userId", currentUid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("TAG", "Firestore Error: ", error);
                        return;
                    }

                    if (value == null) return;

                    List<Recipe> updatedList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        Recipe r = doc.toObject(Recipe.class);
                        r.setId(doc.getId());

                        // 2. Logic: If searchQuery exists, filter by name
                        if (searchQuery != null && !searchQuery.isEmpty()) {
                            if (r.getName() != null && r.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                                updatedList.add(r);
                            }
                        } else {
                            // 3. No search? Show everything for this user
                            updatedList.add(r);
                        }
                    }

                    if (adapter != null) {
                        recipeList_v2 = updatedList;
                        adapter.updateList(updatedList);
                    }

                    // Optional Toast to let user know they are looking at filtered results
                    if (searchQuery != null && !searchQuery.isEmpty() && isAdded()) {
                        Toast.makeText(getContext(), "Showing results for: " + searchQuery, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addRecipe(String name, String desc, String allergies) {
        if (mAuth.getCurrentUser() == null) return;

        String currentUid = mAuth.getCurrentUser().getUid();
        String id = db.collection("recipes").document().getId();

        Recipe newRecipe = new Recipe(id, name, desc, allergies, currentUid);

        db.collection("recipes").document(id)
                .set(newRecipe)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Recipe added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Add failed", Toast.LENGTH_SHORT).show();
                });
    }

    public void deleteRecipe(String id) {
        if (id != null) {
            db.collection("recipes").document(id)
                    .delete()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showAddRecipeDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        EditText nameInput = new EditText(getContext());
        nameInput.setHint("Recipe Name (Required)");
        layout.addView(nameInput);

        EditText descInput = new EditText(getContext());
        descInput.setHint("Instructions / Description");
        layout.addView(descInput);

        EditText allergyInput = new EditText(getContext());
        allergyInput.setHint("Allergies (e.g. Nuts, Dairy)");
        layout.addView(allergyInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Recipe")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String allergies = allergyInput.getText().toString().trim();

                    if (!name.isEmpty()) {
                        addRecipe(name, desc, allergies);
                    } else {
                        Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(Recipe recipe) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Remove '" + recipe.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> deleteRecipe(recipe.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
}