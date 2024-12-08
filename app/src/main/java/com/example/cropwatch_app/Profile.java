package com.example.cropwatch_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";

    private EditText editTextUserName;
    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "Profile activity launched");

        // Initialize UI elements
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonSave = findViewById(R.id.buttonSave);
        TextView textViewIcon = findViewById(R.id.textViewIcon);

        // Initialize logout ImageView
        ImageView logoutImageView = findViewById(R.id.logoutImageView);

        // Load user data from SharedPreferences
        loadUserData();

        // Set click listener for save button
        buttonSave.setOnClickListener(v -> {
            String userName = editTextUserName.getText().toString();
            String email = editTextEmail.getText().toString();

            // Log the entered values (or handle them as needed)
            Log.d(TAG, "Updated User Name: " + userName);
            Log.d(TAG, "Updated Email: " + email);
        });

        // Set click listener for back icon
        textViewIcon.setOnClickListener(v -> {
            Log.d(TAG, "Icon clicked, going back to Page activity");
            Intent intent = new Intent(Profile.this, com.example.cropwatch_app.Page.class);
            startActivity(intent);
        });

        // Set click listener for logout icon
        logoutImageView.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        // Display a confirmation dialog for logging out
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        Log.d(TAG, "Logout confirmed. Clearing session...");

        // Clear session data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CropWatchPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data
        editor.apply();

        // Redirect to the login screen
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finish(); // Prevent going back to the Profile screen
    }

    private void loadUserData() {
        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CropWatchPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Default Name");
        String email = sharedPreferences.getString("user_email", "example@example.com");

        // Set the retrieved data in the EditText fields
        editTextUserName.setText(userName);
        editTextEmail.setText(email);

        Log.d(TAG, "Loaded user data: Name - " + userName + ", Email - " + email);
    }
}
