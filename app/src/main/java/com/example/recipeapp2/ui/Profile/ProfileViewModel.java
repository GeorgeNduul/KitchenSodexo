package com.example.recipeapp2.ui.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            if (newPassword.length() < 6) {
                statusMessage.setValue("Password too short (min 6 chars)");
                return;
            }

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            statusMessage.setValue("Password updated successfully!");
                        } else {
                            statusMessage.setValue("Error: " + task.getException().getMessage());
                        }
                    });
        } else {
            statusMessage.setValue("No user logged in.");
        }
    }
}