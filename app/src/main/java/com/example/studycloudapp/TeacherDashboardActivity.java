package com.example.studycloudapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

public class TeacherDashboardActivity extends BaseActivity {

    private TextView tvWelcome;
    private CardView cardReview, cardPost, cardGradebook;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        tvWelcome = findViewById(R.id.tvWelcomeTeacher);
        cardReview = findViewById(R.id.cardReviewSubmissions);
        cardPost = findViewById(R.id.cardPostAssignment);
        cardGradebook = findViewById(R.id.cardGradebook);
        btnLogout = findViewById(R.id.btnTeacherLogout);

        CardView cardManageCourses = findViewById(R.id.cardManageCourses);
        CardView cardUploadMaterials = findViewById(R.id.cardUploadMaterials);

        cardManageCourses.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageCoursesActivity.class));
        });

        cardUploadMaterials.setOnClickListener(v -> {
            startActivity(new Intent(this, UploadMaterialActivity.class));
        });

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Welcome, Prof. " + username + "!");
        }

        cardReview.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ReviewSubmissionsActivity.class);
            startActivity(intent);
        });

        cardPost.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, CreateAssignmentActivity.class);
            startActivity(intent);
        });

        cardGradebook.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Class Gradebook")
                    .setMessage("Student 1: 92%\nStudent 2: 85%\nStudent 3: 78%")
                    .setPositiveButton("Close", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showReviewDialog() {
        android.widget.EditText etScore = new android.widget.EditText(this);
        etScore.setHint("Enter score (e.g. 95/100)");
        etScore.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("Submissions: Java Mini Project")
                .setMessage("Student: John Doe\nFile: ProjectFinal.zip\nNote: 'Submitted on time bro!'")
                .setView(etScore)
                .setPositiveButton("Submit Grade", (dialog, which) -> {
                    String score = etScore.getText().toString().trim();
                    if (!score.isEmpty()) {
                        Toast.makeText(this, "Grade " + score + " saved for John Doe!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
