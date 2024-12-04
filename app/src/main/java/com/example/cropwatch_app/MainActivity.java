package com.example.cropwatch_app;

import android.content.Intent;
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
    private static final String API_URL = "http://pzf.22b.mytemp.website/api/validate.php"; // Login validation endpoint
    private static final String API_KEY = "357d2dcb60d902fde04044defb45438c8824da242c78f563b1356be83786959c"; // Your API key

    private EditText editTextEmail;
    private EditText editTextPassword;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity launched");

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI elements - matching XML IDs
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

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make the API request for login
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                response -> {
                    Log.d(TAG, "Login response: " + response);  // Log the raw response

                    // Check the response content for success or error
                    if (response.contains("success")) {
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to HomePage
                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                        startActivity(intent);
                        finish();  // Prevent going back to the login screen
                    } else {
                        // Handle error (if response contains "error" or other message)
                        String errorMessage = response.contains("error") ? "Invalid credentials" : "Login failed. Please try again.";
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log any network errors
                    Log.e(TAG, "Network error: " + error.toString());
                    Toast.makeText(MainActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);  // Send the email entered by the user
                params.put("password", password);  // Send the password entered by the user
                params.put("api_key", API_KEY);  // Add the API key to authenticate the request
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Add request to queue
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
