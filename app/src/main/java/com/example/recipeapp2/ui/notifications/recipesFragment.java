package com.example.recipeapp2.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp2.R;
import com.example.recipeapp2.databinding.FragmentRecipeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class recipesFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Recipe> recipeList_v2 = new ArrayList<>();
    private RecyclerView recyclerView;
    private FragmentRecipeBinding binding;
    private RecipeAdapter adapter;
    private FloatingActionButton fab;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recipe, container, false);

        recyclerView = view.findViewById(R.id.recipeRecyclerView);
        fab = view.findViewById(R.id.fabAddRecipe);

        // FIX: Ensure RecyclerView is not null and set layout manager safely
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // FIX: Create a persistent adapter to avoid null crashes
        adapter = new RecipeAdapter(recipeList_v2, new RecipeAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(Recipe recipe) {
                // Future: Click to see details
            }

            @Override
            public void onDeleteClick(Recipe recipe) {
                showDeleteConfirmation(recipe);
            }
        });

        recyclerView.setAdapter(adapter);

        // FloatingActionButton listener
        fab.setOnClickListener(v -> showAddRecipeDialog());

        // FIX: Load Firestore safely AFTER adapter is ready
        loadRecipesFromFirestore();

        return view;
    }


    private void loadRecipesFromFirestore() {

        db.collection("recipes").addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("TAG", "Firestore Error: ", error);
                return;
            }

            if (value == null) return;

            // FIX: Clear list but avoid null adapter issues
            List<Recipe> updatedList = new ArrayList<>();

            for (QueryDocumentSnapshot doc : value) {
                Recipe r = doc.toObject(Recipe.class);
                r.setId(doc.getId());
                updatedList.add(r);
            }

            // FIX: Update adapter safely
            if (adapter != null) {
                recipeList_v2 = updatedList;
                adapter.updateList(updatedList);
            }

            Log.d("TAG", "Loaded recipes: " + updatedList.size());
        });
    }


    public void addRecipe(String name, String desc, String allergies) {
        String id = db.collection("recipes").document().getId();
        Recipe newRecipe = new Recipe(id, name, desc, allergies);

        db.collection("recipes").document(id)
                .set(newRecipe)
                .addOnSuccessListener(unused -> {
                    Log.d("TAG", "Recipe added: " + id);
                    Toast.makeText(getContext(), "Recipe added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Failed to add recipe", e);
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
                        Log.e("TAG", "Delete failed", e);
                        Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Invalid Recipe ID", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}