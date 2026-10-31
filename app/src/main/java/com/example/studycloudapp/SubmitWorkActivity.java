package com.example.studycloudapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SubmitWorkActivity extends AppCompatActivity {

    private Spinner spinnerAssignments, spinnerTeachers;
    private Button btnPickFile, btnSubmit;
    private TextView tvSelectedFile;
    private EditText etNotes;
    private Uri selectedFileUri = null;
    private String currentStudent;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    tvSelectedFile.setText("Selected: " + uri.getLastPathSegment());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_work);

        currentStudent = getIntent().getStringExtra("USERNAME");
        if (currentStudent == null) {
            currentStudent = getSharedPreferences("StudyCloudPrefs", MODE_PRIVATE).getString("LAST_LOGGED_IN_USER", "Student");
        }

        spinnerAssignments = findViewById(R.id.spinnerAssignments);
        spinnerTeachers = findViewById(R.id.spinnerTeachers);
        btnPickFile = findViewById(R.id.btnPickFile);
        btnSubmit = findViewById(R.id.btnSubmitToTeacher);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        etNotes = findViewById(R.id.etSubmissionNotes);

        String[] assignments = {"Java OOP Mini Project", "SQL Database Activity 1", "Networking Quiz Review"};
        ArrayAdapter<String> assignAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, assignments);
        spinnerAssignments.setAdapter(assignAdapter);

        String[] teachers = {"Prof. Smith (Java)", "Prof. Davis (Databases)", "Prof. Miller (Networking)"};
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teachers);
        spinnerTeachers.setAdapter(teacherAdapter);

        btnPickFile.setOnClickListener(v -> filePickerLauncher.launch("*/*"));

        btnSubmit.setOnClickListener(v -> {
            if (spinnerAssignments.getSelectedItem() == null) {
                Toast.makeText(this, "⚠️ Please select an assignment!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedAssignment = spinnerAssignments.getSelectedItem().toString();
            String notes = etNotes.getText().toString().trim();

            if (selectedFileUri == null && notes.isEmpty()) {
                Toast.makeText(this, "⚠️ Please fill up the fields or attach a file!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(this);
            db.addSubmission(currentStudent, selectedAssignment, notes);

            DataManager dataManager = new DataManager(this);
            dataManager.saveSubmission(currentStudent, selectedAssignment, notes);

            Toast.makeText(this, "Submitted at " + new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date()), Toast.LENGTH_SHORT).show();
            finish();
        });

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
