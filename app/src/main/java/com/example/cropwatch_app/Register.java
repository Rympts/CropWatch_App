package com.example.cropwatch_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";
    private static final String REGISTER_URL = "http://pzf.22b.mytemp.website/api/register.php"; // Use HTTPS
    private static final String API_KEY = "357d2dcb60d902fde04044defb45438c8824da242c78f563b1356be83786959c";

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registform);

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI components
        initializeViews();

        // Set click listeners
        registerButton.setOnClickListener(v -> validateAndRegister());
    }

    private void initializeViews() {
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.registerButton);
    }

    private void validateAndRegister() {
        // Get values and trim whitespace
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(firstName, lastName, email, password)) {
            return;
        }

        // Proceed with registration
        performRegistration(firstName, lastName, email, password);
    }

    private boolean validateInputs(String firstName, String lastName, String email, String password) {
        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegistration(String firstName, String lastName, String email, String password) {
        // Disable register button to prevent double submission
        registerButton.setEnabled(false);

        StringRequest registerRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                response -> {
                    Log.d(TAG, "Registration response: " + response);
                    registerButton.setEnabled(true);

                    // Check for success in the response
                    if (response.contains("success")) {
                        Toast.makeText(Register.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                        // Redirect to login screen
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = response.contains("error") ? response : "Registration failed. Please try again.";
                        Toast.makeText(Register.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Registration error: ", error);
                    String errorMessage = "Network error. Please check your connection and try again.";
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Error Data: " + new String(error.networkResponse.data));
                        errorMessage = "Server error. Please try again later.";
                    }
                    Toast.makeText(Register.this, errorMessage, Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_key", API_KEY);
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Add retry policy
        registerRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000, // 30 seconds timeout
                1, // Number of retries
                1.0f // Backoff multiplier
        ));

        requestQueue.add(registerRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
