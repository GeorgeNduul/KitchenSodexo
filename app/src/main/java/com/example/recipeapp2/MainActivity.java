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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply safe insets (optional but fine)
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom);
            return insets;
        });

        // Initialize Firebase correctly
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Button logic
        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomePage.class));
        });
/// checking what type of network the app is using
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        ///  to check for a network for app to connect to
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }
    /// to receive notifications on changes in connection status and network capabilities
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            // Switch to the UI thread to show the Toast
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_LONG).show()
            );
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
            super.onCapabilitiesChanged(network, capabilities);

            // Determine if the connection is metered (e.g., Cellular) or unmetered (e.g., Wi-Fi)
            boolean isUnmetered = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);

            runOnUiThread(() -> {
                if (isUnmetered) {
                    Toast.makeText(MainActivity.this, "Using Unmetered Connection (Wi-Fi)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Using Metered Connection (Data)", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}