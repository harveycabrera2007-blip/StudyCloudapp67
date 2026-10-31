package com.example.studycloudapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupOverlayBar();
    }

    private void setupOverlayBar() {
        ViewGroup root = findViewById(android.R.id.content);
        View overlay = getLayoutInflater().inflate(R.layout.persistent_overlay_bar, root, false);
        root.addView(overlay);

        LinearLayout navHome = overlay.findViewById(R.id.navHome);
        LinearLayout navAnnounce = overlay.findViewById(R.id.navAnnouncements);
        LinearLayout navProfile = overlay.findViewById(R.id.navProfile);
        LinearLayout navLogout = overlay.findViewById(R.id.navLogout);

        SharedPreferences prefs = getSharedPreferences("StudyCloudPrefs", MODE_PRIVATE);
        String currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) {
            currentUsername = prefs.getString("LAST_LOGGED_IN_USER", "User");
        }
        final String activeUser = currentUsername;
        final String activeRole = prefs.getString(activeUser + "_ROLE", "Student");

        navHome.setOnClickListener(v -> {
            Intent intent;
            if ("Admin".equalsIgnoreCase(activeRole)) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else if ("Teacher".equalsIgnoreCase(activeRole)) {
                intent = new Intent(this, TeacherDashboardActivity.class);
            } else {
                intent = new Intent(this, StudentDashboardActivity.class);
            }
            intent.putExtra("USERNAME", activeUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        navAnnounce.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("📢 Campus Broadcasts")
                    .setMessage("• Midterm exams begin next Monday.\n• System maintenance scheduled for Sunday midnight.\n• Library hours extended until 9 PM.")
                    .setPositiveButton("Close", null)
                    .show();
        });

        navProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("User Profile")
                    .setMessage("Logged in as: " + activeUser + "\nRole: " + activeRole)
                    .setPositiveButton("OK", null)
                    .show();
        });

        navLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        navAnnounce.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationCenterActivity.class);
            startActivity(intent);
        });
    }
}
