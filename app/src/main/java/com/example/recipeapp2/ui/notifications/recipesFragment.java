package com.example.recipeapp2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.recipeapp2.databinding.FragmentRecipeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;

public class recipesFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private recipesViewModel viewModel;
    private RecipeAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(recipesViewModel.class);

        setupRecyclerView();

        // Observe recipes from ViewModel
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null && adapter != null) {
                adapter.updateList(recipes);
            }
        });

        // Click FAB to show the dialog
        binding.fabAddRecipe.setOnClickListener(v -> showAddRecipeDialog());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Logic for clicking an item (like opening details) can go here
        });
        binding.recipeRecyclerView.setAdapter(adapter);
    }

    private void showAddRecipeDialog() {
        // 1. Create a layout to hold three input fields
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        final EditText nameInput = new EditText(getContext());
        nameInput.setHint("Recipe Name (e.g., Pancakes)");
        layout.addView(nameInput);

        final EditText descInput = new EditText(getContext());
        descInput.setHint("Description (e.g., Fluffy and sweet)");
        layout.addView(descInput);

        // NEW: Allergy Input Field
        final EditText allergyInput = new EditText(getContext());
        allergyInput.setHint("Allergies (e.g., Eggs, Milk, Nuts)");
        layout.addView(allergyInput);

        // 2. Build and show the Dialog
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add New Recipe")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String allergies = allergyInput.getText().toString().trim();

                    if (!name.isEmpty()) {
                        // 3. Send all three strings to the ViewModel
                        viewModel.addRecipe(name, desc, allergies);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}