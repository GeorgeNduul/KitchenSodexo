package com.example.recipeapp2;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signUpButton, loginButton, forgotPasswordButton;
    private TextInputEditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initView();
        setupNetworkMonitoring();

        signUpButton.setOnClickListener(v -> signUpUser());
        loginButton.setOnClickListener(v -> signInUser());
        forgotPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void initView() {
        signUpButton = findViewById(R.id.button);
        loginButton = findViewById(R.id.btnLogin);
        forgotPasswordButton = findViewById(R.id.btnForgotPassword);
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
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            handleAuthResult(true, "Sign Up Successful");
                        } else {
                            handleFirebaseError(task.getException());
                        }
                    });
        }
    }

    private void signInUser() {
        String email = getTrimmedText(emailField);
        String password = getTrimmedText(passwordField);

        if (validateInput(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            handleAuthResult(true, "Login Successful");
                        } else {
                            handleFirebaseError(task.getException());
                        }
                    });
        }
    }

    private void resetPassword() {
        String email = getTrimmedText(emailField);
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Enter your email to reset password");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                    } else {
                        handleFirebaseError(task.getException());
                    }
                });
    }

    private void handleFirebaseError(Exception e) {
        String message = "Authentication Failed";

        if (e instanceof FirebaseAuthUserCollisionException) {
            message = "This email address is already registered.";
            emailField.setError("Email already exists");
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Invalid password or email format.";
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            message = "No account found with this email.";
            emailField.setError("Account not found");
        } else if (e != null) {
            message = e.getLocalizedMessage();
        }

        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private String getTrimmedText(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void handleAuthResult(boolean success, String message) {
        if (success) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomePage.class));
            finish();
        }
    }
//this piece of code is not mine and i have refrenced it
    private void setupNetworkMonitoring() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ConnectivityManager.class);
        if (connectivityManager != null) {
            connectivityManager.requestNetwork(networkRequest, networkCallback);
        }
    }
    //this piece of code is not mine and i have refrenced it
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            runOnUiThread(() -> {
                // Re-enable buttons when internet returns
                signUpButton.setEnabled(true);
                loginButton.setEnabled(true);
                signUpButton.setAlpha(1.0f); // Make them look active
                loginButton.setAlpha(1.0f);
                Toast.makeText(MainActivity.this, "Online: Features Restored", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onLost(@NonNull Network network) {
            runOnUiThread(() -> {
                // Disable buttons to prevent "Ghost" clicks
                signUpButton.setEnabled(false);
                loginButton.setEnabled(false);
                signUpButton.setAlpha(0.5f); // Make them look "greyed out"
                loginButton.setAlpha(0.5f);
                Toast.makeText(MainActivity.this, "Offline: Check your connection", Toast.LENGTH_LONG).show();
            });
        }
    };
}
