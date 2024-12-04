package com.example.cropwatch_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "Profile activity launched");

        // Find the EditText for username
        EditText editTextUserName = findViewById(R.id.editTextUserName);
        // Find the EditText for email
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        // Find the Button for saving
        Button buttonSave = findViewById(R.id.buttonSave);
        // Find the TextView for the icon
        TextView textViewIcon = findViewById(R.id.textViewIcon);

        // Set an OnClickListener for the save button
        buttonSave.setOnClickListener(v -> {
            // Get text from EditText fields
            String userName = editTextUserName.getText().toString();
            String email = editTextEmail.getText().toString();

            // Log the values or handle them as needed
            Log.d(TAG, "User Name: " + userName);
            Log.d(TAG, "Email: " + email);

            // No output displayed; input text remains in EditText
        });

        // Set an OnClickListener for the TextView icon
        textViewIcon.setOnClickListener(v -> {
            Log.d(TAG, "Icon clicked, going back to Page activity");
            // Create an Intent to start the Page activity
            Intent intent = new Intent(Profile.this, com.example.cropwatch_app.Page.class);
            startActivity(intent);  // Navigate to Page
        });
    }
}
