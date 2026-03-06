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

        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null && adapter != null) {
                adapter.updateList(recipes);
            }
        });

        binding.fabAddRecipe.setOnClickListener(v -> showAddRecipeDialog());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Future: Open recipe details
        });
        binding.recipeRecyclerView.setAdapter(adapter);
    }

    private void showAddRecipeDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        final EditText nameInput = new EditText(getContext());
        nameInput.setHint("Recipe Name");
        layout.addView(nameInput);

        final EditText descInput = new EditText(getContext());
        descInput.setHint("Description");
        layout.addView(descInput);

        final EditText allergyInput = new EditText(getContext());
        allergyInput.setHint("Allergies (Optional)");
        layout.addView(allergyInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add New Recipe")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String allergies = allergyInput.getText().toString().trim();

                    if (!name.isEmpty()) {
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