package com.example.recipeapp2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class recipesFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private recipesViewModel viewModel; // Added ViewModel
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // 1. Initialize UI
        recyclerView = view.findViewById(R.id.recipeRecyclerView);
        fab = view.findViewById(R.id.fabAddRecipe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Setup Adapter
        adapter = new RecipeAdapter(new ArrayList<>(), new RecipeAdapter.OnRecipeClickListener() {
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

        // 3. Setup ViewModel & Observers
        viewModel = new ViewModelProvider(this).get(recipesViewModel.class);

        // Capture search query from HomeFragment
        final String searchQuery = getArguments() != null ? getArguments().getString("search_query") : null;

        // Observe the data broadcast from the ViewModel
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                List<Recipe> filteredList = new ArrayList<>();

                // Handle filtering logic within the observer
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    for (Recipe r : recipes) {
                        if (r.getName() != null && r.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                            filteredList.add(r);
                        }
                    }
                    adapter.updateList(filteredList);
                    if (isAdded()) Toast.makeText(getContext(), "Filtered: " + searchQuery, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.updateList(recipes);
                }
            }
        });

        // Start fetching data
        viewModel.fetchRecipes();

        fab.setOnClickListener(v -> showAddRecipeDialog());

        return view;
    }

    // CREATE Logic
    public void addRecipe(String name, String desc, String allergies) {
        if (mAuth.getCurrentUser() == null) return;

        String currentUid = mAuth.getCurrentUser().getUid();
        String id = db.collection("recipes").document().getId();

        Recipe newRecipe = new Recipe(id, name, desc, allergies, currentUid);

        db.collection("recipes").document(id)
                .set(newRecipe)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Recipe added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Add failed", Toast.LENGTH_SHORT).show());
    }

    // DELETE Logic
    public void deleteRecipe(String id) {
        if (id != null) {
            db.collection("recipes").document(id)
                    .delete()
                    .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show());
        }
    }

    // DIALOGS
    private void showAddRecipeDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        EditText nameInput = new EditText(getContext());
        nameInput.setHint("Recipe Name");
        layout.addView(nameInput);

        EditText descInput = new EditText(getContext());
        descInput.setHint("Instructions");
        layout.addView(descInput);

        EditText allergyInput = new EditText(getContext());
        allergyInput.setHint("Allergies");
        layout.addView(allergyInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("New KitchenSodexo Recipe")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (!name.isEmpty()) {
                        addRecipe(name, descInput.getText().toString().trim(), allergyInput.getText().toString().trim());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmation(Recipe recipe) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete")
                .setMessage("Delete " + recipe.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteRecipe(recipe.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
}