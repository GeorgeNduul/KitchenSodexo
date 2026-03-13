package com.example.recipeapp2;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signUpButton, loginButton;
    private TextInputEditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initView();
        setupNetworkMonitoring();

        // Listeners for both actions
        signUpButton.setOnClickListener(v -> signUpUser());
        loginButton.setOnClickListener(v -> signInUser());
    }

    private void initView() {
        signUpButton = findViewById(R.id.button);
        loginButton = findViewById(R.id.btnLogin);
        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom);
            return insets;
        });
    }

    private void signUpUser() {
        String email = getTrimmedText(emailField);
        String password = getTrimmedText(passwordField);

        if (validateInput(email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> handleAuthResult(task.isSuccessful(), "Sign Up Successful"));
        }
    }

    private void signInUser() {
        String email = getTrimmedText(emailField);
        String password = getTrimmedText(passwordField);

        if (validateInput(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> handleAuthResult(task.isSuccessful(), "Login Successful"));
        }
    }

    private String getTrimmedText(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleAuthResult(boolean success, String message) {
        if (success) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomePage.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNetworkMonitoring() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        if (connectivityManager != null) {
            connectivityManager.requestNetwork(networkRequest, networkCallback);
        }
    }

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_LONG).show());
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
            super.onCapabilitiesChanged(network, capabilities);
            boolean isUnmetered = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            runOnUiThread(() -> {
                String type = isUnmetered ? "Wi-Fi (Unmetered)" : "Data (Metered)";
                Toast.makeText(MainActivity.this, "Using " + type, Toast.LENGTH_SHORT).show();
            });
        }
    };
}