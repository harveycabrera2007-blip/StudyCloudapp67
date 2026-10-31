package com.example.studycloudapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends BaseActivity  {

    private TextView tvAdminSubtitle;
    private CardView cardManageUsers, cardAnnouncement, cardLogs;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvAdminSubtitle = findViewById(R.id.tvAdminSubtitle);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardAnnouncement = findViewById(R.id.cardAnnouncement);
        cardLogs = findViewById(R.id.cardSystemLogs);
        btnLogout = findViewById(R.id.btnAdminLogout);

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            tvAdminSubtitle.setText("Logged in as: " + username);
        }

        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });

        cardAnnouncement.setOnClickListener(v -> showBroadcastDialog());

        cardLogs.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("System Status")
                    .setMessage("• Database: Online\n• Cloud Sync: Active (Ping 24ms)\n• Authentication: EdgeToEdge Enabled\n• No critical errors recorded.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Admin session closed", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showBroadcastDialog() {
        EditText inputMessage = new EditText(this);
        inputMessage.setHint("Write announcement message...");

        new AlertDialog.Builder(this)
                .setTitle("Broadcast to All Users")
                .setMessage("This will push a notification to students and teachers.")
                .setView(inputMessage)
                .setPositiveButton("Send Broadcast", (dialog, which) -> {
                    String msg = inputMessage.getText().toString().trim();
                    if (!msg.isEmpty()) {
                        Toast.makeText(this, "Broadcast sent: \"" + msg + "\"", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}