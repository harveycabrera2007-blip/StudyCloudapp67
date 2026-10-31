package com.example.studycloudapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "⚠️ Please fill up the fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(MainActivity.this);

            if (!db.authenticateUser(username, password)) {
                Toast.makeText(MainActivity.this, "⚠️ Invalid username or password!", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = db.getUserRole(username);

            getSharedPreferences("StudyCloudPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("LAST_LOGGED_IN_USER", username)
                    .putString(username + "_ROLE", role)
                    .apply();

            Intent intent;
            if ("Admin".equalsIgnoreCase(role)) {
                intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
            } else if ("Teacher".equalsIgnoreCase(role)) {
                intent = new Intent(MainActivity.this, TeacherDashboardActivity.class);
            } else {
                if (!db.isStudentEnrolled(username)) {
                    intent = new Intent(MainActivity.this, EnrollmentActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                }
            }

            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
