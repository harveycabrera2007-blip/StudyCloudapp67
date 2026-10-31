package com.example.studycloudapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class EnrollmentActivity extends AppCompatActivity {

    private Spinner spinnerCourses;
    private DatabaseHelper db;
    private DataManager dataManager;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        db = new DatabaseHelper(this);
        dataManager = new DataManager(this);
        username = getIntent().getStringExtra("USERNAME");

        spinnerCourses = findViewById(R.id.spinnerCourses);
        Button btnConfirmEnroll = findViewById(R.id.btnConfirmEnroll);

        ArrayList<String> courses = db.getAllCourses();
        if (courses.isEmpty()) {
            courses.addAll(dataManager.getCourses());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        spinnerCourses.setAdapter(adapter);

        btnConfirmEnroll.setOnClickListener(v -> {
            if (spinnerCourses.getSelectedItem() == null) {
                Toast.makeText(this, "⚠️ Please select a course!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedCourse = spinnerCourses.getSelectedItem().toString();

            String assignedSection = db.enrollStudent(username, selectedCourse);
            dataManager.enrollStudent(username, selectedCourse);

            Toast.makeText(this, "Enrolled in " + selectedCourse + "! Section: " + assignedSection, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(EnrollmentActivity.this, StudentDashboardActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
