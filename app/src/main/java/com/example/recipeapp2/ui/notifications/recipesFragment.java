package com.example.recipeapp2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipeapp2.databinding.FragmentRecipeBinding;

import java.util.ArrayList;

public class recipesFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private recipesViewModel viewModel;
    private RecipeAdapter adapter; // Ensure you have created the RecipeAdapter class

    public recipesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. Initialize View Binding
        binding = FragmentRecipeBinding.inflate(inflater, container, false);

        // 2. Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(recipesViewModel.class);

        // 3. Setup RecyclerView
        setupRecyclerView();

        // 4. Observe the data from ViewModel
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null && adapter != null) {
                adapter.updateList(recipes);
            }
        });

        // 5. FAB click to add a dummy recipe
        binding.fabAddRecipe.setOnClickListener(v -> {
            viewModel.addRecipe("New KitchenSodexo Recipe", "Click to see details");
        });

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initializing adapter with empty list and a click listener
        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Logic for when a recipe is clicked
        });
        binding.recipeRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}