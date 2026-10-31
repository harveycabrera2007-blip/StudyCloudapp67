package com.example.studycloudapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAssignmentActivity extends AppCompatActivity {

    private EditText etTitle, etDeadline, etDesc;
    private Button btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        etTitle = findViewById(R.id.etAssignTitle);
        etDeadline = findViewById(R.id.etAssignDeadline);
        etDesc = findViewById(R.id.etAssignDesc);
        btnPublish = findViewById(R.id.btnPublishAssignment);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPublish.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String deadline = etDeadline.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            DatabaseHelper db = new DatabaseHelper(this);
            db.addAssignment(title, deadline, desc);

            if (title.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "⚠️ Please fill up the fields!", Toast.LENGTH_SHORT).show();
            } else {
                DataManager dm = new DataManager(this);
                dm.publishAssignment(title, deadline, desc);

                Toast.makeText(this, "Assignment published! Students notified.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
