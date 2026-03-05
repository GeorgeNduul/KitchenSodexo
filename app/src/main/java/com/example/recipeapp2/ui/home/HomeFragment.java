package com.example.recipeapp2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.recipeapp2.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    // 1. Declare binding properly
    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // 2. Initialize the binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 3. Return the root of the binding (NOT a new inflated layout)
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Example: If you want to change text dynamically
        binding.welcome.setText("Welcome Back!");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 4. Important: Clear binding to avoid memory leaks
        binding = null;
    }
}