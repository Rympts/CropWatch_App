package com.example.cropwatch_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Page extends AppCompatActivity {

    private static final String TAG = "Page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_page);

        Log.d(TAG, "Page activity launched");

        // Find the ImageView for the home icon
        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        // Find the ImageView for the profile icon
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        // Find the ImageView for the weather icon
        ImageView imageViewWeather = findViewById(R.id.baseline_track_changes_24);
        // Find the ImageView for the task icon
        ImageView imageViewTask = findViewById(R.id.baseline_task_24);

        // Set an OnClickListener to handle the home icon click
        imageViewHome.setOnClickListener(v -> {
            Log.d(TAG, "Home icon clicked");
            Intent intent = new Intent(Page.this, HomePage.class);
            startActivity(intent);  // Navigate to HomePage
        });

        // Set an OnClickListener to handle the profile icon click
        imageViewProfile.setOnClickListener(v -> {
            Log.d(TAG, "Profile icon clicked");
            Intent intent = new Intent(Page.this, Profile.class);
            startActivity(intent);  // Navigate to Profile
        });

        // Set an OnClickListener to handle the weather icon click
        imageViewWeather.setOnClickListener(v -> {
            Log.d(TAG, "Weather icon clicked");
            Intent intent = new Intent(Page.this, Weather.class);
            startActivity(intent);  // Navigate to Weather
        });

        // Set an OnClickListener to handle the task icon click
        imageViewTask.setOnClickListener(v -> {
            Log.d(TAG, "Task icon clicked");
            Intent intent = new Intent(Page.this, Task.class);
            startActivity(intent);  // Navigate to Task
        });


    }
}
