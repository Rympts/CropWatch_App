package com.example.cropwatch_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class Weather extends AppCompatActivity {
    private static final String TAG = "Weather";
    private static final String API_KEY = "cffcd7227eae5dc1e6ad49559af5bc12"; // Your API key

    // UI elements
    private EditText editTextLocation;
    private TextView city1Name, city1Temp, city1Desc;
    private ImageView city1Icon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Initialize UI elements
        editTextLocation = findViewById(R.id.editTextLocation);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        city1Name = findViewById(R.id.city1Name);
        city1Temp = findViewById(R.id.city1Temp);
        city1Desc = findViewById(R.id.city1Desc);
        city1Icon = findViewById(R.id.city1Icon);

        // Set initial visibility to GONE (until data is fetched)
        city1Name.setVisibility(View.GONE);
        city1Temp.setVisibility(View.GONE);
        city1Desc.setVisibility(View.GONE);
        city1Icon.setVisibility(View.GONE);

        // Set up button click listener to fetch weather data
        buttonSubmit.setOnClickListener(v -> {
            String location = editTextLocation.getText().toString().trim();
            if (!location.isEmpty()) {
                getWeatherData(location);
            } else {
                city1Name.setText("Please enter a location.");
                city1Name.setVisibility(View.VISIBLE); // Show the prompt in the placeholder
                city1Temp.setVisibility(View.GONE);
                city1Desc.setVisibility(View.GONE);
                city1Icon.setVisibility(View.GONE);
            }
        });

        // Click listeners for icons (Home, Profile, etc.)
        setupIconClickListeners();
    }

    // Fetch weather data from OpenWeatherMap API
    private void getWeatherData(String location) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + API_KEY + "&units=metric";

        // Create JSON request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::onResponse,
                error -> {
                    Log.e(TAG, "Error: " + error.getMessage());
                    city1Name.setText("Error fetching weather data.");
                    city1Name.setVisibility(View.VISIBLE);
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    // Handle the JSON response
    private void onResponse(JSONObject response) {
        displayWeatherData(response);
    }

    // Extract and display relevant weather information from the response
    @SuppressLint("SetTextI18n")
    private void displayWeatherData(JSONObject response) {
        try {
            String cityName = response.getString("name");
            String weatherDescription = response.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = response.getJSONObject("main").getDouble("temp");

            // Update UI elements with fetched weather data
            city1Name.setText(cityName);
            city1Temp.setText(temperature + "°C");
            city1Desc.setText(weatherDescription);

            // Make views visible after populating them
            city1Name.setVisibility(View.VISIBLE);
            city1Temp.setVisibility(View.VISIBLE);
            city1Desc.setVisibility(View.VISIBLE);
            city1Icon.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            city1Name.setText("Error processing weather data.");
            city1Name.setVisibility(View.VISIBLE);
        }
    }

    // Setup listeners for icons (Home, Profile, etc.)
    private void setupIconClickListeners() {
        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        ImageView imageViewHeadline = findViewById(R.id.imageViewHeadline);
        ImageView imageViewTrackChanges = findViewById(R.id.baseline_track_changes_24);
        ImageView imageViewTask = findViewById(R.id.baseline_task_24);

        imageViewHome.setOnClickListener(v -> {
            Log.d(TAG, "Home icon clicked");
            finish(); // Close Weather activity and return to previous
        });

        imageViewProfile.setOnClickListener(v -> {
            Log.d(TAG, "Profile icon clicked");
            startActivity(new Intent(Weather.this, com.example.cropwatch_app.Profile.class));
        });

        imageViewHeadline.setOnClickListener(v -> {
            Log.d(TAG, "Headline icon clicked");
            // Add your navigation logic here
        });

        imageViewTrackChanges.setOnClickListener(v -> {
            Log.d(TAG, "Track changes icon clicked");
            startActivity(new Intent(Weather.this, com.example.cropwatch_app.Task.class));
        });

        imageViewTask.setOnClickListener(v -> {
            Log.d(TAG, "Task icon clicked");
            startActivity(new Intent(Weather.this, com.example.cropwatch_app.Task.class));
        });
    }
}
