package com.example.cropwatch_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Task extends AppCompatActivity {

    private static final String TAG = "Task";
    private LinearLayout taskContainer; // Container for displaying tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Log.d(TAG, "Task activity launched");

        // Find the ImageView for the home icon
        ImageView imageViewHome = findViewById(R.id.imageViewHome);
        // Find the ImageView for the profile icon
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        // Find the ImageView for the weather icon
        ImageView imageViewWeather = findViewById(R.id.baseline_track_changes_24);
        // Initialize the task container
        taskContainer = findViewById(R.id.taskContainer); // Make sure you have a LinearLayout with this ID in your XML

        // Set OnClickListeners for icons to navigate to different pages
        imageViewHome.setOnClickListener(v -> {
            Log.d(TAG, "Home icon clicked");
            Intent intent = new Intent(Task.this, HomePage.class);
            startActivity(intent);
        });

        imageViewProfile.setOnClickListener(v -> {
            Log.d(TAG, "Profile icon clicked");
            Intent intent = new Intent(Task.this, com.example.cropwatch_app.Profile.class);
            startActivity(intent);
        });

        imageViewWeather.setOnClickListener(v -> {
            Log.d(TAG, "Weather icon clicked");
            Intent intent = new Intent(Task.this, Weather.class);
            startActivity(intent);
        });

        // Retrieve the list of tasks passed from HomePage
        Intent intent = getIntent();
        ArrayList<String> addedTasks = intent.getStringArrayListExtra("addedTasks");

        // Check for null or empty task list and display tasks
        if (addedTasks != null && !addedTasks.isEmpty()) {
            for (String task : addedTasks) {
                displayTask(task); // Display each task in the container
            }
        } else {
            TextView noTasksTextView = new TextView(this);
            noTasksTextView.setText("No tasks added.");
            taskContainer.addView(noTasksTextView);
        }
    }

    // Method to display a task in the task container
    private void displayTask(String task) {
        TextView taskTextView = new TextView(this);
        taskTextView.setText(task);
        taskTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add the task TextView to the task container
        taskContainer.addView(taskTextView);
    }
}
