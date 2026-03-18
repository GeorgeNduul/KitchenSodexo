package com.example.recipeapp2.ui.Profile;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipeapp2.MainActivity;
import com.example.recipeapp2.databinding.FragmentProfileBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private static final String TAG = "KitchenSodexo_Profile";
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private ImageCapture imageCapture;
    private final Executor cameraExecutor = Executors.newSingleThreadExecutor();
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    Toast.makeText(requireContext(), "Permissions denied.", Toast.LENGTH_SHORT).show();
                } else {
                    initCameraProvider();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        // 1. Display Current User Info
        FirebaseUser user = mAuth.getCurrentUser();
        // Tip: You can use binding.tvProfileHeader.setText("Hello, " + user.getEmail()) here if you'd like

        // 2. Camera Setup
        binding.bCapture1.setEnabled(false);
        binding.bCapture1.setOnClickListener(v -> capturePhoto());
        checkPermissionsAndStart();

        // 3. Password Update Logic
        binding.btnUpdatePassword.setOnClickListener(v -> {
            String newPass = binding.etNewPassword.getText().toString().trim();
            if (!newPass.isEmpty()) {
                viewModel.updatePassword(newPass);
            } else {
                Toast.makeText(getContext(), "Enter a new password", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Logout Logic (Added for KitchenSodexo)
        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            // These flags clear the backstack so the user can't "back" into the app
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // 5. Observe ViewModel Status for Feedback
        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                if (message.contains("successfully")) {
                    binding.etNewPassword.setText("");
                }
            }
        });
    }

    // --- CAMERA METHODS ---

    //checks a list of permission the camera needs
    private void checkPermissionsAndStart() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
//This loops through each permission and checks if the user has granted it.
        boolean needsRequest = false;
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), p) != PackageManager.PERMISSION_GRANTED) {
                needsRequest = true;
                break;
            }
        }
//- If permissions are missing → request them
//- If permissions are already granted → start the camera immediately
        if (needsRequest) {
            permissionLauncher.launch(permissions.toArray(new String[0]));
        } else {
            initCameraProvider();
        }
    }
//This asks CameraX for a camera provider, which is the object that controls the camera.
    private void initCameraProvider() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                startCameraX(cameraProviderFuture.get());
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error initializing camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
//- Adds a listener that runs when the camera is ready
//- Calls startCameraX() to actually open the camera
    private void startCameraX(@NonNull ProcessCameraProvider cameraProvider) {
        //This clears any previous camera use cases (important when reopening the camera).
        cameraProvider.unbindAll();
        //selects the front camera
        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

        //This sends the camera feed to your PreviewView in the layout.
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView1.getSurfaceProvider());
//This configures the camera to take fast photos
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
//This is the moment the camera actually turns on.
        try {
            cameraProvider.bindToLifecycle(getViewLifecycleOwner(), selector, preview, imageCapture);
            //enables the capture button
            binding.bCapture1.setEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }
// captures the photo and save it
    private void capturePhoto() {
        if (imageCapture == null) return;
// this sets the file name and type
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "KitchenSodexo_" + System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/KitchenSodexo");
        }
// this tells the CameraX where to save the photo
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(
                requireContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
        ).build();

        // this takes the image in the background
        imageCapture.takePicture(options, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            // shows a toast on the main thread
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults results) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Profile Photo Saved!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(@NonNull ImageCaptureException e) {
                Log.e(TAG, "Capture failed", e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}