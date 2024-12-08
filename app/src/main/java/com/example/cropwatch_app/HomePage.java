package com.example.cropwatch_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        // Load tasks from database
        loadTasksFromDatabase();

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

    // Method to load tasks from the database
    private void loadTasksFromDatabase() {
        String url = "https://kristoffer.helioho.st/cropwatch_api/load_tasks.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                // Assuming the response is in JSON format
                JSONObject jsonResponse = new JSONObject(response);
                if (!jsonResponse.getString("status").equals("No tasks found")) {
                    // Assuming the tasks are in a "tasks" array
                    JSONArray tasksArray = jsonResponse.getJSONArray("tasks");
                    addedTasks.clear();  // Clear the addedTasks list before adding new tasks
                    for (int i = 0; i < tasksArray.length(); i++) {
                        JSONObject task = tasksArray.getJSONObject(i);
                        String taskName = task.getString("task_name");
                        String startDate = task.getString("start_date");
                        String endDate = task.getString("end_date");
                        String description = task.getString("description");

                        // Add task to the UI
                        addNewContainerWithDateAndDescription(taskName, startDate, endDate, description);
                        addedTasks.add(taskName); // Store task name to prevent duplicates
                    }
                }
            } catch (JSONException e) {
                Log.e("loadTasksFromDatabase", "JSON Parsing error: " + e.getMessage());
                Toast.makeText(HomePage.this, "Error loading tasks.", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("loadTasksFromDatabase", "Error: " + error.getMessage());
            Toast.makeText(HomePage.this, "Error loading tasks.", Toast.LENGTH_SHORT).show();
        });

        Volley.newRequestQueue(this).add(request);
    }

    // Method to add the task with a delete button
    @SuppressLint("SetTextI18n")
    private void addNewContainerWithDateAndDescription(String taskName, String startDate, String endDate, String description) {
        LinearLayout newContainer = new LinearLayout(this);
        newContainer.setOrientation(LinearLayout.VERTICAL);
        newContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create and set up TextViews
        TextView taskTextView = new TextView(this);
        taskTextView.setText(taskName + " - " + startDate + " - " + endDate);
        newContainer.addView(taskTextView);

        TextView descriptionTextView = new TextView(this);
        descriptionTextView.setText(description);
        newContainer.addView(descriptionTextView);

        // Create a Delete button for each task
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
        newContainer.addView(deleteButton);

        // Add the new container to the main container
        container.addView(newContainer);

        // Handle delete button click to remove the task from the database and UI
        deleteButton.setOnClickListener(v -> {
            // Delete the task from the database
            deleteTaskFromDatabase(taskName);

            // Remove the task from the UI
            container.removeView(newContainer);
        });
    }

    // Method to show the predefined task list
    private void showTaskList() {
        if (container.indexOfChild(taskListView) != -1) {
            container.removeView(taskListView);
        }
        container.addView(taskListView, 0); // Add to the top

        // Handle item click to show a date picker for the selected task
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTask = tasks[position];
            showDatePicker(selectedTask);
        });
    }

    // Method to show the date picker
    private void showDatePicker(String selectedTask) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, yearSelected, monthSelected, daySelected) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(yearSelected, monthSelected, daySelected);

            String selectedDate = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
            Toast.makeText(HomePage.this, "Task: " + selectedTask + ", Date selected: " + selectedDate, Toast.LENGTH_SHORT).show();

            generateSchedule(selectedTask, selectedCalendar);

            container.removeView(taskListView);  // Remove task list view after selection
        }, year, month, day);
        datePickerDialog.show();
    }

    // Generate and save the schedule for the selected task
    private void generateSchedule(String selectedTask, Calendar startCalendar) {
        int durationDays = 0;
        String taskDescription = "";

        // Determine task duration based on selected task
        switch (selectedTask) {
            case "Harvesting":
                durationDays = 5;
                taskDescription = "Harvesting takes 1-4 days involves gathering the matured rice.";
                break;
            case "Planting":
                durationDays = 3;
                taskDescription = "Planting takes 1-2 days involves sowing seeds in the field.";
                break;
            case "Watering":
                durationDays = 1;
                taskDescription = "Watering takes 1 day involves irrigating the field.";
                break;
            case "Fertilizing":
                durationDays = 2;
                taskDescription = "Fertilizing 1-2 days involves applying fertilizer to the crops.";
                break;
        }

        // Save the task to the database
        saveTaskToDatabase(selectedTask, startCalendar, durationDays, taskDescription);

        // Add the task to the UI
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateStr = sdf.format(startCalendar.getTime());
        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, durationDays - 1);
        String endDateStr = sdf.format(endCalendar.getTime());

        addNewContainerWithDateAndDescription(selectedTask, startDateStr, endDateStr, taskDescription);
    }

    // Method to save task to the database
    private void saveTaskToDatabase(String taskName, Calendar startDate, int duration, String description) {
        String url = "https://kristoffer.helioho.st/cropwatch_api/save_task.php";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateStr = sdf.format(startDate.getTime());

        Calendar endCalendar = (Calendar) startDate.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, duration - 1);
        String endDateStr = sdf.format(endCalendar.getTime());

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> Toast.makeText(HomePage.this, "Task saved successfully.", Toast.LENGTH_SHORT).show(), error -> Toast.makeText(HomePage.this, "Error saving task: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_name", taskName);
                params.put("start_date", startDateStr);
                params.put("end_date", endDateStr);
                params.put("description", description);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // Method to delete task from the database
    private void deleteTaskFromDatabase(String taskName) {
        String url = "https://kristoffer.helioho.st/cropwatch_api/delete_task.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> Toast.makeText(HomePage.this, "Task deleted successfully.", Toast.LENGTH_SHORT).show(), error ->    Toast.makeText(HomePage.this, "Error deleting task: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_name", taskName);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload tasks when the user comes back to this activity
        loadTasksFromDatabase();
    }
}
