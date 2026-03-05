package com.example.recipeapp2.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipeapp2.R;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes != null ? recipes : new ArrayList<>();
        this.listener = listener;
    }

    // This allows your Fragment to send new data from the ViewModel to the list
    public void updateList(List<Recipe> newList) {
        this.recipes = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // R.layout.item_recipe refers to res/layout/item_recipe.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        // These calls now work because of the Getters in Recipe.java
        holder.name.setText(recipe.getName());
        holder.desc.setText(recipe.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClick(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc;
        public ViewHolder(View itemView) {
            super(itemView);
            // Matches IDs in item_recipe.xml
            name = itemView.findViewById(R.id.recipeName);
            desc = itemView.findViewById(R.id.recipeDesc);
        }
    }
}