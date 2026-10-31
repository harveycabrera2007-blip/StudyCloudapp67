package com.example.studycloudapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationCenterActivity extends BaseActivity {

    private static final String CHANNEL_ID = "studycloud_pink_channel";
    private Button btnToast, btnPush, btnSuccess, btnWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnToast = findViewById(R.id.btnToastAlert);
        btnPush = findViewById(R.id.btnPushNotification);
        btnSuccess = findViewById(R.id.btnSuccessAlert);
        btnWarning = findViewById(R.id.btnWarningAlert);

        // Initialize Android Notification Channel
        createNotificationChannel();

        // 1. Toast Alert
        btnToast.setOnClickListener(v -> {
            Toast.makeText(this, "🌸 StudyCloud: This is a fast in-app toast notification!", Toast.LENGTH_SHORT).show();
        });

        // 2. Real System Status Bar Notification
        btnPush.setOnClickListener(v -> triggerSystemNotification());

        // 3. Success Pop-up Alert
        btnSuccess.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("✅ Operation Completed")
                    .setMessage("Your course submission was successfully sent and backed up to the database.")
                    .setPositiveButton("Awesome", null)
                    .show();
        });

        // 4. Warning Pop-up Alert (with warning symbol)
        btnWarning.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("⚠️ System Notice")
                    .setMessage("⚠️ Submission deadline is near! Please ensure all project files are uploaded before midnight.")
                    .setPositiveButton("I Understand", null)
                    .show();
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "StudyCloud Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Academic updates, submissions, and teacher notices.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void triggerSystemNotification() {
        // Android 13+ (API 33) runtime permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("StudyCloud Update 📢")
                .setContentText("Prof. Smith published a new module for BSIT-1A.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
            Toast.makeText(this, "Notification posted to status bar!", Toast.LENGTH_SHORT).show();
        }
    }
}