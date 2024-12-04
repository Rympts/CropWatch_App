package com.example.cropwatch_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    private LinearLayout container;
    private ListView taskListView;
    private final String[] tasks = {"Harvesting", "Planting", "Watering", "Fertilizing"}; // Predefined tasks
    private final ArrayList<String> addedTasks = new ArrayList<>(); // Store added tasks

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Set up insets for the layout to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Initialize container to add dynamic views
        container = findViewById(R.id.container);

        // Initialize ListView for task selection
        taskListView = new ListView(this);
        ArrayAdapter<String> taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        taskListView.setAdapter(taskAdapter);

        // Load tasks from SharedPreferences
        loadSavedTasks();

        // Set up listener for the Add button to show predefined tasks
        findViewById(R.id.addButton).setOnClickListener(v -> showTaskList());

        // Set up click listener for navigating to the Page activity (Next Page button)
        TextView textViewIcon = findViewById(R.id.textViewIcon);
        textViewIcon.setOnClickListener(v -> {
            // Pass the addedTasks to the Page activity
            Intent intent = new Intent(HomePage.this, Page.class);
            intent.putStringArrayListExtra("taskList", addedTasks); // Pass the task list
            startActivity(intent);
            Log.d("HomePage", "Navigating to Page activity");
        });



    }

    // Method to load saved tasks from SharedPreferences
    private void loadSavedTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE);
        String savedTasks = sharedPreferences.getString("tasks", "");

        // Check if there are saved tasks
        if (!savedTasks.isEmpty()) {
            String[] taskArray = savedTasks.split(";");

            for (String taskInfo : taskArray) {
                String[] parts = taskInfo.split(",");
                String taskName = parts[0];
                String description = parts[2];
                Calendar startCalendar = Calendar.getInstance();
                addNewContainerWithDateAndDescription(taskName, startCalendar, Integer.parseInt(parts[3]), description);
            }
        }
    }

    // Method to display the list of predefined tasks
    private void showTaskList() {
        // Clear previous views only if they are tasks
        if (container.indexOfChild(taskListView) != -1) {
            container.removeView(taskListView);
        }

        // Add the task ListView to the container at the top
        container.addView(taskListView, 0); // Add to the top of the container

        // Handle item clicks to select a task and show the date picker
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTask = tasks[position];
            showDatePicker(selectedTask);
        });
    }

    // Method to show the date picker dialog
    private void showDatePicker(String selectedTask) {
        // Get the current date as default values
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog to select the start date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, yearSelected, monthSelected, daySelected) -> {
            // Set the selected date into a new calendar object
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(yearSelected, monthSelected, daySelected);

            // Format the selected date
            String selectedDate = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
            Toast.makeText(HomePage.this, "Task: " + selectedTask + ", Date selected: " + selectedDate, Toast.LENGTH_SHORT).show();

            // Generate the schedule based on the selected task using the selected calendar date
            generateSchedule(selectedTask, selectedCalendar);

            // Remove the task list view only if the user adds a task
            container.removeView(taskListView);
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    // Method to generate the schedule based on the selected task and start date
    private void generateSchedule(String selectedTask, Calendar startCalendar) {
        int durationDays; // Initialize durationDays variable
        String taskDescription = ""; // Initialize task description

        // Determine duration based on the selected task
        switch (selectedTask) {
            case "Harvesting":
                durationDays = 5;
                taskDescription = "Harvesting takes 1-4 days involves gathering the matured rice.";
                break;
            case "Planting":
                durationDays = 3;
                taskDescription = "Planting takes 1-2 days  involves sowing seeds in the field.";
                break;
            case "Watering":
                durationDays = 1;
                taskDescription = "Watering takes 1 day  involves irrigating the field.";
                break;
            case "Fertilizing":
                durationDays = 2;
                taskDescription = "Fertilizing 1-2 days involves applying fertilizer to the crops.";
                break;
            default:
                durationDays = 1;
                break;
        }

        // Add the task to SharedPreferences
        saveTaskToPreferences(selectedTask, startCalendar, durationDays, taskDescription);

        // Call the method to add the new task with date range and description
        addNewContainerWithDateAndDescription(selectedTask, startCalendar, durationDays, taskDescription);
    }

    // Method to save a task to SharedPreferences
    private void saveTaskToPreferences(String taskName, Calendar startCalendar, int duration, String description) {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String startDate = sdf.format(startCalendar.getTime());

        // Calculate end date
        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, duration - 1);
        String endDate = sdf.format(endCalendar.getTime());

        String newTask = taskName + "," + startDate + " - " + endDate + "," + description + "," + duration;

        // Append new task to the existing task list
        String currentTasks = sharedPreferences.getString("tasks", "");
        if (!currentTasks.isEmpty()) {
            currentTasks += ";";
        }
        currentTasks += newTask;

        editor.putString("tasks", currentTasks);
        editor.apply();
    }

    // Method to add a new container with task name, date range, and description
    // Method to add a new container with task name, date range, and description
    @SuppressLint("SetTextI18n")
    private void addNewContainerWithDateAndDescription(String taskName, Calendar startCalendar, int duration, String description) {
        // Create a new horizontal LinearLayout to hold the task, date range, and description
        LinearLayout newContainer = new LinearLayout(this);
        newContainer.setOrientation(LinearLayout.VERTICAL); // Vertical orientation
        newContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Calculate end date
        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, duration - 1);

        // Format the date range
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String dateRange = sdf.format(startCalendar.getTime()) + " - " + sdf.format(endCalendar.getTime());

        // Create a TextView for task name and date range
        TextView taskTextView = new TextView(this);
        taskTextView.setText(taskName + " - " + dateRange);
        taskTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a TextView for the description
        TextView descriptionTextView = new TextView(this);
        descriptionTextView.setText(description);
        descriptionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a Delete button to remove the task
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add task name, date range, description, and delete button to the new container
        newContainer.addView(taskTextView);
        newContainer.addView(descriptionTextView);
        newContainer.addView(deleteButton);

        // Add the new container to the main layout
        container.addView(newContainer);
        addedTasks.add(taskName + " - " + dateRange); // Store added task

        // Handle delete button click to remove the container
        deleteButton.setOnClickListener(v -> {
            // Remove from SharedPreferences
            removeTaskFromPreferences(taskName, dateRange, description, duration);

            // Remove the view from the container
            container.removeView(newContainer);
            addedTasks.remove(taskName + " - " + dateRange); // Remove from the list
        });
    }

    // Method to remove a task from SharedPreferences
    private void removeTaskFromPreferences(String taskName, String dateRange, String description, int duration) {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String currentTasks = sharedPreferences.getString("tasks", "");
        String taskToRemove = taskName + "," + dateRange + "," + description + "," + duration;

        // Check if the task exists and remove it
        if (currentTasks.contains(taskToRemove)) {
            currentTasks = currentTasks.replace(taskToRemove + ";", ""); // Remove the specific task
            currentTasks = currentTasks.replaceAll(";;", ";").replaceAll("^;|;$", ""); // Clean up

            // Save the updated task list back to SharedPreferences
            editor.putString("tasks", currentTasks);
            editor.apply();
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show(); // Optional: notify user
        }
    }
}
