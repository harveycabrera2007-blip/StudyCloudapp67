package com.example.studycloudapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;

public class StudentDashboardActivity extends BaseActivity {

    private TextView tvWelcome, tvNotifBadge;
    private FrameLayout btnNotification;
    private CardView cardSubmit, cardHistory, cardGrades;
    private DataManager dataManager;
    private DatabaseHelper db;
    private String currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dataManager = new DataManager(this);
        db = new DatabaseHelper(this);
        currentStudent = getIntent().getStringExtra("USERNAME");
        if (currentStudent == null) {
            currentStudent = getSharedPreferences("StudyCloudPrefs", MODE_PRIVATE).getString("LAST_LOGGED_IN_USER", "Student");
        }

        tvWelcome = findViewById(R.id.tvWelcomeStudent);
        tvNotifBadge = findViewById(R.id.tvNotifBadge);
        btnNotification = findViewById(R.id.btnStudentNotification);
        cardSubmit = findViewById(R.id.cardSubmitWork);
        cardHistory = findViewById(R.id.cardSubmissionHistory);
        cardGrades = findViewById(R.id.cardViewGrades);

        String studentCourse = db.getUserCourse(currentStudent);
        String studentSection = db.getUserSection(currentStudent);
        tvWelcome.setText("Welcome, " + currentStudent + "!\n[" + studentCourse + " - " + studentSection + "]");

        checkNotifications();

        btnNotification.setOnClickListener(v -> showNotificationsDialog());

        cardSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubmitWorkActivity.class);
            intent.putExtra("USERNAME", currentStudent);
            startActivity(intent);
        });

        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentHistoryActivity.class);
            intent.putExtra("USERNAME", currentStudent);
            startActivity(intent);
        });

        cardGrades.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Current Grades")
                    .setMessage("• Prelim: 91\n• Midterm: 89\n• Status: Passed")
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotifications();
    }

    private void checkNotifications() {
        int unread = dataManager.getUnreadNotificationCount();
        if (unread > 0) {
            tvNotifBadge.setVisibility(View.VISIBLE);
            tvNotifBadge.setText(String.valueOf(unread));
        } else {
            tvNotifBadge.setVisibility(View.GONE);
        }
    }

    private void showNotificationsDialog() {
        ArrayList<String> assignments = dataManager.getAllPublishedAssignments();
        StringBuilder sb = new StringBuilder();

        if (assignments.isEmpty()) {
            sb.append("No new assignments or activities posted yet.");
        } else {
            for (String item : assignments) {
                String[] parts = item.split(":::");
                sb.append("📢 ").append(parts[0])
                        .append("\n📅 Deadline: ").append(parts.length > 1 ? parts[1] : "")
                        .append("\n📝 ").append(parts.length > 2 ? parts[2] : "")
                        .append("\n\n");
            }
        }

        dataManager.clearNotifications();
        checkNotifications();

        new AlertDialog.Builder(this)
                .setTitle("Teacher Announcements & Tasks")
                .setMessage(sb.toString())
                .setPositiveButton("Understood", null)
                .show();
    }
}
