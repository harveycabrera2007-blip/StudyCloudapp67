package com.example.studycloudapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etRegPassword;
    private Spinner spinnerRole;
    private Button btnSubmitRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);

        String[] roles = {"Student", "Teacher", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roles
        );
        spinnerRole.setAdapter(adapter);

        btnSubmitRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();

            if (spinnerRole.getSelectedItem() == null) {
                Toast.makeText(RegisterActivity.this, "⚠️ Please select a role!", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedRole = spinnerRole.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "⚠️ Please fill up the fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(RegisterActivity.this);

            if (db.checkUsernameExists(username)) {
                Toast.makeText(RegisterActivity.this, "⚠️ Username already taken! Choose another.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isSaved = db.registerUser(username, password, selectedRole);
            if (isSaved) {
                Toast.makeText(RegisterActivity.this, "Account created as " + selectedRole + "!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Error creating account. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
