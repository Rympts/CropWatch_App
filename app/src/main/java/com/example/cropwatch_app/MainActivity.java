package com.example.cropwatch_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String API_URL = "http://pzf.22b.mytemp.website/api/validate.php";
    private static final String API_KEY = "357d2dcb60d902fde04044defb45438c8824da242c78f563b1356be83786959c";
    private static final String PREFS_NAME = "CropWatchPrefs";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);

        if (userEmail != null) {
            // If credentials exist, navigate to the HomePage
            Log.d(TAG, "User already logged in. Navigating to HomePage.");
            navigateToHomePage();
            return; // Prevent loading the login screen
        }

        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity launched");


        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.loginButton);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        // Set up login button click listener
        loginButton.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            performLogin();
        });

        // Set up register button click listener
        buttonRegister.setOnClickListener(v -> {
            Log.d(TAG, "Register button clicked");
            Intent intent = new Intent(MainActivity.this, com.example.cropwatch_app.Register.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        loginRequest(email, password);
    }

    private void loginRequest(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                response -> {
                    Log.d(TAG, "Login response: " + response);

                    if (response.contains("success")) {
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Save user credentials to SharedPreferences
                        saveUserCredentials(email, password);

                        // Navigate to the HomePage
                        navigateToHomePage();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.toString());
                    Toast.makeText(MainActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("api_key", API_KEY);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void saveUserCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.apply();
        Log.d(TAG, "User credentials saved.");
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
        finish();
    }

    public static void logoutUser(AppCompatActivity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved data
        editor.apply();
        Log.d(TAG, "User logged out.");

        // Redirect to Login screen
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
