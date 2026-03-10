package com.example.recipeapp2.ui.notifications;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp2.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onDeleteClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    public void updateList(List<Recipe> newList) {
        this.recipes = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.desc.setText(recipe.getDescription());

        // Allergy Logic: Only show if text exists
        if (recipe.getAllergies() != null && !recipe.getAllergies().isEmpty()) {
            holder.allergies.setText("Allergies: " + recipe.getAllergies());
            holder.allergies.setVisibility(View.VISIBLE);
        } else {
            holder.allergies.setVisibility(View.GONE);
        }

        // Click on the card for details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });

        // Click on the Trash Icon to delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, allergies;
        ImageButton btnDelete; // New Button Reference

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipeName);
            desc = itemView.findViewById(R.id.recipeDesc);
            allergies = itemView.findViewById(R.id.recipeAllergies);
            btnDelete = itemView.findViewById(R.id.btnDelete); // Bind the button
        }
    }
}