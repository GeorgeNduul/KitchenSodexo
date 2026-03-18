package com.example.recipeapp2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recipeapp2.R;
import com.example.recipeapp2.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public HomeFragment() {
        // Required empty public constructor
    }
//binding links my code with my UI
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Setup Autocomplete Recommendations
        setupSearchSuggestions();

        // 2. Handle the Search Button Click
        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.etSearchRecipe.getText().toString().trim();
// passing data between fragments
            Bundle bundle = new Bundle();
            bundle.putString("search_query", query);

            Navigation.findNavController(v).navigate(R.id.navigation_recipe, bundle);
        });

        binding.welcome.setText("Welcome Back!");
    }

    private void setupSearchSuggestions() {
        //checks to see user is logged in
        if (mAuth.getCurrentUser() == null) return;

        //to fetch only the current users recipe from firebase
        String currentUid = mAuth.getCurrentUser().getUid();

        // Fetch  current user's recipes from firebase to fill the suggestion list
        db.collection("recipes")
                .whereEqualTo("userId", currentUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> suggestions = new ArrayList<>();
                    // get recipe name from documents
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String recipeName = doc.getString("name");
                        if (recipeName != null) {
                            suggestions.add(recipeName);
                        }
                    }

                    // This adapter holds the list of recipe names and tells Android how to display them in the dropdown.
                    if (getContext() != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                suggestions
                        );
                        binding.etSearchRecipe.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}